package com.codeit.monew.domain.article.infrastructure;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.infrastructure.exception.naver.NaverApiAuthenticationException;
import com.codeit.monew.domain.article.infrastructure.exception.naver.NaverApiRateLimitException;
import com.codeit.monew.domain.article.infrastructure.exception.naver.NaverApiServerException;
import com.codeit.monew.domain.article.infrastructure.naver.client.NaverArticleClient;
import com.codeit.monew.domain.article.infrastructure.naver.dto.NaverApiResponse;
import com.codeit.monew.domain.article.infrastructure.naver.mapper.NaverArticleMapper;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interestkeyword.InterestKeyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverArticleCollector implements ArticleCollector{

    private final NaverArticleClient naverArticleClient;
    private final NaverArticleMapper naverArticleMapper;

    @Override
    public List<ArticleCreateRequest> collect(List<Interest> interests) {
        return interests.stream()
                .flatMap(this::collectByInterest)
                .toList();
    }

    private Stream<ArticleCreateRequest> collectByInterest(Interest interest) {
        String query = buildQuery(interest);

        log.info("검색 쿼리: {}", query);

        return searchQuery(query, interest);
    }
    // name + keywords 전부 합치기
    private String buildQuery(Interest interest) {
        String keywords = interest.getKeywords().stream()
                .map(InterestKeyword::getKeyword)
                .collect(Collectors.joining(" "));

        return interest.getName() + " " + keywords;
    }

    // client에 요청
    private Stream<ArticleCreateRequest> searchQuery(String query, Interest interest) {
        try {
            NaverApiResponse response = naverArticleClient.searchArticle(query, 100, 1);
            if(response.items().isEmpty()) {
                log.info("검색 결과 없음 : query = {}", query);
                return Stream.empty();
            }

            log.info("검색 성공: query = {}", query);
            return response.items().stream()
                    .map(item -> naverArticleMapper.toArticleCreateRequest(item, interest));

        } catch (NaverApiAuthenticationException e) {
            log.error("네이버 API 인증 실패: error = {}", e.getMessage());
            return Stream.empty();
        } catch (NaverApiRateLimitException e) {
            log.warn("네이버 API RATE LIMIT 초과: query = {} error = {}", query, e.getMessage());
            return Stream.empty();
        } catch (NaverApiServerException e) {
            log.error("네이버 API 서버 오류: query = {} error = {}", query,  e.getMessage());
            return Stream.empty();
        } catch (Exception e) {
            log.error("예상치 못한 오류 keyword={}, error={}", query, e.getMessage());
            return Stream.empty();
        }
    }

}
