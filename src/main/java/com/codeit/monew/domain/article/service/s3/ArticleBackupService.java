package com.codeit.monew.domain.article.service.s3;

import com.codeit.monew.domain.article.dto.response.ArticleBackupDto;
import com.codeit.monew.domain.article.dto.response.ArticleRestoreResultDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleBackupService {

    private final S3Client s3Client;
    private final ArticleRepository articleRepository;
    private final ObjectMapper objectMapper;
    private final ArticleRestoreProcessor articleRestoreProcessor;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public void backupArticles(LocalDate date) {

        List<Article> articles = articleRepository.findByPublishDate(date);

        if (articles.isEmpty()) {
            log.info("{} 백업할 파일이 없습니다.", date);
            return;
        }

        List<ArticleBackupDto> dtos = articles.stream()
                .map(ArticleBackupDto::fromEntity)
                .toList();

        try {
            // JSON 변환
            String backupContent = objectMapper.writeValueAsString(dtos);
            String key = String.format("articles/%04d/%02d/%02d.json",
                    date.getYear(), date.getMonthValue(), date.getDayOfMonth());

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType("application/json")
                    .build();

            s3Client.putObject(request, RequestBody.fromString(backupContent));
            log.info("백업 성공: {}", key);

        } catch (JsonProcessingException e) {
            log.error("백업 실패: ", e);
            throw new RuntimeException("백업 데이터를 json으로 변환하는데 실패했습니다.", e);
        }
    }

    public List<ArticleRestoreResultDto> restoredArticles(LocalDate from, LocalDate to) {

        LocalDate limit = LocalDate.now().minusDays(7);

        if (from.isAfter(limit)) return List.of();

        LocalDate end = to.isBefore(limit) ? to : limit;

        List<ArticleRestoreResultDto> results = new ArrayList<>();

        for (LocalDate date = from; !date.isAfter(end); date = date.plusDays(1)) {
            List<UUID> restoreArticleIds = articleRestoreProcessor.s3Restore(date);
            results.add(
                    new ArticleRestoreResultDto(LocalDateTime.now(), restoreArticleIds, (long) restoreArticleIds.size())
            );
        }

        return results;
    }
}