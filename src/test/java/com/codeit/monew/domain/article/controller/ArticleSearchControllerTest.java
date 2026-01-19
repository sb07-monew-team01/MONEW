package com.codeit.monew.domain.article.controller;

import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleSearchController.class)
class ArticleSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private ArticleService articleService;

    @Nested
    @DisplayName("기사 조회")
    class SearchArticleTest {

        private UUID userId;
        private UUID articleId;
        private ArticleDto dto;

        @BeforeEach
        void setUp() {
            userId = UUID.randomUUID();
            articleId = UUID.randomUUID();
            dto = new ArticleDto(
                    articleId, "NAVER", "주소", "두쫀쿠",
                    LocalDateTime.now(), "먹어보고 싶다.", 0L, 0L,
                    false);
        }

        @Test
        @DisplayName("뉴스 기사 단건 조회")
        void getArticle() throws Exception {
            // given
            when(articleService.searchByUserIdAndArticleId(userId, articleId))
                    .thenReturn(dto);

            
            // when & then
            mockMvc.perform(get("/api/articles/{articleId}", articleId)
                    .header("Monew-Request-User-ID", userId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(articleId.toString()))
                    .andExpect(jsonPath("$.title").value("두쫀쿠"))
                    .andExpect(jsonPath("$.summary").value("먹어보고 싶다."));

            verify(articleService, times(1)).searchByUserIdAndArticleId(userId, articleId);
        }
    }
}