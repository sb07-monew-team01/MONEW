package com.codeit.monew.domain.article.controller;

import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.service.ArticleService;
import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;
import com.codeit.monew.domain.articleView.service.ArticleViewService;
import com.codeit.monew.global.dto.PageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @MockitoBean
    private ArticleViewService articleViewService;

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

        @Test
        @DisplayName("기사 기사 목록 조회")
        void getArticlesCursorPaging() throws Exception {
            // given

            List<ArticleDto> articles = List.of(dto);
            PageResponse<ArticleDto> pages = new PageResponse<>(articles, null, null, 10, 1, false);

            when(articleService.searchByKeyword(any(ArticleSearchRequest.class), eq(userId)))
                    .thenReturn(pages);

            // when & then
            mockMvc.perform(get("/api/articles")
                            .param("keyword", "두쫀쿠")
                            .param("limit", "10")
                            .header("Monew-Request-User-ID", userId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(articleId.toString()))
                    .andExpect(jsonPath("$.content[0].summary").value("먹어보고 싶다."));
        }
    }
    
    @Nested
    @DisplayName("기사 뷰")
    class CreateArticleViewTest {

        private UUID userId;
        private UUID articleId;
        private ArticleViewDto dto;

        @BeforeEach
        void setUp() {
            userId = UUID.randomUUID();
            articleId = UUID.randomUUID();
            dto = new ArticleViewDto(
                    UUID.randomUUID(), userId, LocalDateTime.now(), articleId,
                    "NAVER", "주소", "기사 뷰 생성",
                    LocalDateTime.now(), "완료", 0L, 0L);
        }

        @Test
        @DisplayName("기사 뷰 등록")
        void addArticleView() throws Exception {
            // given

            when(articleViewService.createArticleView(articleId, userId)).thenReturn(dto);

            // when & then
            mockMvc.perform(post("/api/articles/{articleId}/article-views", articleId)
                    .header("Monew-Request-User-ID", userId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.articleId").value(articleId.toString()))
                    .andExpect(jsonPath("$.viewedBy").value(userId.toString()));
        }
    }
}