package com.codeit.monew.domain.article.service.s3;

import com.codeit.monew.domain.article.dto.response.ArticleRestoreResultDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleBackupServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private S3Client s3Client;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    @Mock
    private ArticleRestoreProcessor articleRestoreProcessor;

    @InjectMocks
    private ArticleBackupService articleBackupService;

    @Nested
    @DisplayName("기사 백업")
    class BackupArticle {

        @Test
        @DisplayName("기사가 있으면 S3에 JSON으로 저장한다.")
        void backupArticle() {
            // given
            LocalDate from = LocalDate.of(2026, 1, 10);
            Article article = ArticleFixture.createDefaultEntity();

            when(articleRepository.findByPublishDate(from)).thenReturn(List.of(article));

            // when
            articleBackupService.backupArticles(from);

            // then
            verify(articleRepository, times(1)).findByPublishDate(from);
            verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        @DisplayName("기사가 없으면 백업을 안한다.")
        void backupArticle_articleListEmpty() {
            // given
            LocalDate from = LocalDate.of(2026, 1, 10);
            when(articleRepository.findByPublishDate(from)).thenReturn(List.of());

            // when
            articleBackupService.backupArticles(from);

            // then
            verify(articleRepository, times(1)).findByPublishDate(from);
            verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        @DisplayName("JSON으로 변환에 실패하면 백업을 실패한다.")
        void backupArticle_failToConvertToJson() throws JsonProcessingException {
            // given
            LocalDate from = LocalDate.of(2026, 1, 10);
            Article article = ArticleFixture.createDefaultEntity();

            when(articleRepository.findByPublishDate(from)).thenReturn(List.of(article));

            doThrow(JsonProcessingException.class)
                    .when(objectMapper).writeValueAsString(any());

            // when & then
            assertThatThrownBy(() -> articleBackupService.backupArticles(from))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("백업 데이터를 json으로 변환하는데 실패했습니다.");

            verify(articleRepository, times(1)).findByPublishDate(from);
            verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }
    }
    
    @Nested
    @DisplayName("기사 복구")
    class RestoreArticle {
        
        @Test
        @DisplayName("기사를 복구 할 수 있다.")
        void articleRestore() {
            // given
            UUID id = UUID.randomUUID();

            LocalDate from = LocalDate.of(2026, 1, 1);
            LocalDate to = LocalDate.of(2026, 1, 3);

            when(articleRestoreProcessor.s3Restore(any(LocalDate.class)))
                    .thenReturn(List.of(id));

            // when
            articleBackupService.restoredArticles(from, to);
            
            // then
            verify(articleRestoreProcessor, times(3)).s3Restore(any(LocalDate.class));
        }

        @Test
        @DisplayName("복구 시작 날짜가 자동 복구 날짜(2일전 기사)보다 뒤 라면, 복구를 하지 않는다.")
        void articleRestore_fromIsAfterLimit_EmptyList() {
            // given
            LocalDate from = LocalDate.now().minusDays(1);
            LocalDate to = LocalDate.now();

            
            // when
            List<ArticleRestoreResultDto> result = articleBackupService.restoredArticles(from, to);
            
            // then
            assertThat(result).isEmpty();
            verify(articleRestoreProcessor, never()).s3Restore(any(LocalDate.class));
        }

        @Test
        @DisplayName("복구 끝 날짜가 자동 백업 날짜(2일전)보다 뒤 라면, 자동 백업 날 전까지만 복구한다.")
        void articleRestore_toIsBeforeLimit_findArticleBetweenFromAndLimit() {
            // given
            LocalDate from = LocalDate.now().minusDays(3);
            LocalDate to = LocalDate.now();

            // when
            List<ArticleRestoreResultDto> result =  articleBackupService.restoredArticles(from, to);

            // then
            assertThat(result).hasSize(2);
            verify(articleRestoreProcessor, times(2)).s3Restore(any(LocalDate.class));
        }
    }
}