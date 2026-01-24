package com.codeit.monew.domain.article.controller;

import com.codeit.monew.domain.article.exception.ArticleNotFoundException;
import com.codeit.monew.domain.article.service.ArticleService;
import com.codeit.monew.domain.article.service.s3.ArticleBackupService;
import com.codeit.monew.domain.articleView.service.ArticleViewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
class ArticleDeleteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService articleService;
    @MockitoBean
    private ArticleViewService articleViewService;
    @MockitoBean
    private S3Client s3Client;
    @MockitoBean
    ArticleBackupService articleBackupService;

    @Nested
    @DisplayName("논리 삭제 테스트")
    class ArticleDelete {
        @Test
        @DisplayName("article Id가 존재하면 기사를 논리삭제할 수 있다. 204를 반환한다.")
        void deleteArticle_success() throws Exception {
            // given
            UUID articleId = UUID.randomUUID();
            willDoNothing().given(articleService).softDelete(articleId);

            // when
            mockMvc.perform(delete("/api/articles/{articleId}", articleId))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            // then
            then(articleService).should().softDelete(articleId);
        }

        @Test
        @DisplayName("존재하지 않는 articleId이면 404를 반환한다.")
        void deleteArticle_fail_notFound() throws Exception {
            // given
            UUID articleId = UUID.randomUUID();
            willThrow(new ArticleNotFoundException(articleId)).given(articleService).softDelete(articleId);

            // when
            mockMvc.perform(delete("/api/articles/{articleId}", articleId))
                    .andDo(print())
                    .andExpect(status().isNotFound());

            // then
            then(articleService).should().softDelete(articleId);
        }
    }
    
    @Nested
    @DisplayName("물리 삭제 테스트")
    class ArticleDeleteHard {
        @Test
        @DisplayName("article Id가 존재하면 기사를 물리삭제할 수 있다. 204를 반환한다.")
        void deleteHardArticle_success() throws Exception {
            // given
            UUID articleId = UUID.randomUUID();
            willDoNothing().given(articleService).hardDelete(articleId);

            // when
            mockMvc.perform(delete("/api/articles/{articleId}/hard", articleId))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            // then
            then(articleService).should().hardDelete(articleId);
        }

        @Test
        @DisplayName("article Id가 존재하지 않으면 404 반환한다.")
        void deleteHardArticle_fail_notFound() throws Exception {
            // given
            UUID articleId = UUID.randomUUID();
            willThrow(new ArticleNotFoundException(articleId)).given(articleService).hardDelete(articleId);

            // when
            mockMvc.perform(delete("/api/articles/{articleId}/hard", articleId))
                    .andDo(print())
                    .andExpect(status().isNotFound());

            // then
            then(articleService).should().hardDelete(articleId);
        }
    }
}