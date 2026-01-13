package com.codeit.monew.article.service;

import com.codeit.monew.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.article.fixture.ArticleFixture;
import com.codeit.monew.article.fixture.ArticleSearchRequestFixture;
import com.codeit.monew.common.dto.PageResponse;
import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.article.service.ArticleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceSearchTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Nested
    class Search {

        @Test
        @DisplayName("""
        마지막 기사로부터 다음 페이즈를 위한 커서를 생성한다.
        PageResponse 반환에 성공한다.
        """)
        void convertArticleCursorPageResponse() {
            // given
            Article article1 =  ArticleFixture.createEntity(ArticleCreateRequestFixture.createDummy(0, -1));
            LocalDateTime lastDate = article1.getPublishDate();

            Pageable pageable = PageRequest.of(0, 10);
            Page<Article> articlePage = new PageImpl<>(List.of(article1), pageable, 11);

            when(articleRepository.findByKeywordAndSource(any())).thenReturn(articlePage);

            // when
            ArticleSearchRequest request = ArticleSearchRequestFixture.createDefault();
            PageResponse<ArticleDto> articlePages = articleService.searchByKeyword(request);

            // then
            assertThat(articlePages.nextCursor()).isEqualTo(lastDate.toString());
            assertThat(articlePages.hasNext()).isTrue();
        }
    }
}
