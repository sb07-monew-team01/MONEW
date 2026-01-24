package com.codeit.monew.domain.article.infrastructure.service;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.infrastructure.ArticleCollector;
import com.codeit.monew.domain.article.infrastructure.CollectReport;
import com.codeit.monew.domain.article.infrastructure.CollectedArticleMapper;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.interestuser.repository.InterestUserRepository;
import com.codeit.monew.domain.notification.dto.request.NotificationCreateRequest;
import com.codeit.monew.domain.notification.dto.request.NotificationCreateRequestList;
import com.codeit.monew.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleCollectServiceImpl implements ArticleCollectService {

    private final List<ArticleCollector> articleCollectors;
    private final ArticleRepository articleRepository;
    private final InterestRepository interestRepository;
    private final CollectedArticleMapper collectedArticleMapper;

    private final NotificationService notificationService;
    private final InterestUserRepository interestUserRepository;


    @Override
    public CollectReport collectAndSave() {
        List<Interest> interests = interestRepository.findAll();

        // 관심사가 없다면 종료
        if (interests.isEmpty()) {
            return new CollectReport(0, 0);
        }

        // 수집된 기사들 중, 중복이 있다면, 중복을 제거하고 리스트로 생성
        List<ArticleCreateRequest> collectedArticles =
                articleCollectors.stream()
                        .flatMap(c -> c.collect(interests).stream())
                        .collect(Collectors.toMap(
                                ArticleCreateRequest::sourceUrl,
                                request -> request,
                                (oldRequest, newRequest) -> oldRequest
                        ))
                        .values().stream()
                        .toList();

        List<String> collectedUrls = collectedArticles.stream()
                .map(ArticleCreateRequest::sourceUrl)
                .toList();

        // 수집된 기사가 없다면 종료
        if (collectedUrls.isEmpty()) {
            return new CollectReport(0, 0);
        }

        // 1000개씩 끊어서 존재하는 데이터인지 판단.
        // 존재하는 데이터라면, existingUrlSet 추가
        Set<String> existingUrlSet = new HashSet<>();
        int batchSize = 1000;
        for (int i = 0; i < collectedUrls.size(); i += batchSize) {
            List<String> batch = collectedUrls.subList(i, Math.min(i + batchSize, collectedUrls.size()));
            existingUrlSet.addAll(articleRepository.findExistingUrlsIn(batch));
        }

        //  ExsistingUrlSet과 collectArtilce를 비교하여
        // 중복 제거된 List<ArticleRequest>
        List<ArticleCreateRequest> newRequests = collectedArticles.stream()
                .filter(request -> !existingUrlSet.contains(request.sourceUrl()))
                .toList();

        // 존재하지 않는 URL만 리스트화 -> newArticles
        List<Article> newArticles = newRequests.stream()
                .map(collectedArticleMapper::toEntity)
                .toList();


        // hibernate.jdbc.batch_size 설정이 적용되어 있어 JDBC batch로 묶여 전송된다.
        articleRepository.saveAll(newArticles);

        if (!newRequests.isEmpty()) {
            createdNotifications(interests, newRequests);
        }

        return new CollectReport(collectedArticles.size(), newArticles.size());
    }

    public void createdNotifications(List<Interest> interests, List<ArticleCreateRequest> newRequests) {
        // <관심사ID, 관심사 이름>
        Map<UUID, String> interestIdToName = interests.stream()
                .collect(Collectors.toMap(
                        Interest::getId,
                        Interest::getName));

        // 1. 이번에 저장된 기사들이 속한 관심사 ID 집합 구하기
        Set<UUID> interestIds = newRequests.stream()
                .map(ArticleCreateRequest::interestId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!interestIds.isEmpty()) {
            // 2. 관심사별 구독자(InterestUser) 엔티티들 조회
            List<InterestUser> interestUsers =
                    interestUserRepository.findAllByInterestIdIn(interestIds);

            // 3. 관심사 ID -> 유저 ID 리스트 맵으로 변환
            Map<UUID, List<UUID>> interestToUserIds = interestUsers.stream()
                    .collect(Collectors.groupingBy(
                            iu -> iu.getInterest().getId(),
                            Collectors.mapping(iu -> iu.getUser().getId(), Collectors.toList())
                    ));

            List<NotificationCreateRequest> notificationRequests = new ArrayList<>();

            // 4. "관심사별 기사 개수" 집계 (newRequests DTO를 직접 사용하여 단순화)
            Map<UUID, Long> countByInterestId = newRequests.stream()
                    .filter(req -> req.interestId() != null)
                    .collect(Collectors.groupingBy(
                            ArticleCreateRequest::interestId,
                            Collectors.counting()
                    ));

            // 5. 집계된 관심사별로 알림 생성
            for (Map.Entry<UUID, Long> entry : countByInterestId.entrySet()) {
                UUID interestId = entry.getKey();
                int count = entry.getValue().intValue();

                List<UUID> subscriberIds =
                        interestToUserIds.getOrDefault(interestId, List.of());

                String interestName = interestIdToName.get(interestId);

                for (UUID userId : subscriberIds) {
                    notificationRequests.add(NotificationCreateRequest.of(
                            userId,
                            interestId,
                            interestName,
                            count
                    ));
                }
            }

            // 6. 실제 알림 저장
            if (!notificationRequests.isEmpty()) {
                notificationService.createAllByInterest(
                        new NotificationCreateRequestList(notificationRequests)
                );
            }
        }
    }
}


