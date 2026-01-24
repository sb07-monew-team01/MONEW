package com.codeit.monew.domain.article.service.s3;

import com.codeit.monew.domain.article.dto.response.ArticleBackupDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleRestoreProcessor {

    private final S3Client s3Client;
    private final ArticleRepository articleRepository;
    private final ObjectMapper objectMapper;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Transactional
    public List<UUID> s3Restore(LocalDate date) {

        String key = String.format("articles/%04d/%02d/%02d.json",
                date.getYear(), date.getMonthValue(), date.getDayOfMonth());

        try {
            String jsonContent = downloadFile(key);
            List<ArticleBackupDto> backupDtos = objectMapper.readValue(
                    jsonContent, objectMapper.getTypeFactory().constructCollectionType(List.class, ArticleBackupDto.class));

            Set<String> existsUrls = articleRepository.findSourceUrlsByPublishDateBetween(
                    date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay()
            );

            List<Article> lostArticles = backupDtos.stream()
                    .filter(dto -> !existsUrls.contains(dto.sourceUrl()))
                    .map(ArticleBackupDto::toEntity).toList();

            if(!lostArticles.isEmpty()) {
                articleRepository.saveAll(lostArticles);
                return lostArticles.stream().map(Article::getId).toList();
            }
        } catch (S3Exception e) {
            log.info("S3에 {} 백업 파일이 존재하지 않습니다.", date);
        }
        catch (Exception e) {
            log.warn("{} 복구 중 문제가 발생했습니다.", date, e);
            throw new RuntimeException(e);
        }
        return List.of();
    }

    public String downloadFile(String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> objectAsBytes
                = s3Client.getObjectAsBytes(getObjectRequest);

        return new String(objectAsBytes.asByteArray(), StandardCharsets.UTF_8);
    }
}
