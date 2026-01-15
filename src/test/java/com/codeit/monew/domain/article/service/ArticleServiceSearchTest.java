package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.domain.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.fixture.ArticleSearchRequestFixture;
import com.codeit.monew.global.dto.PageResponse;
import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceSearchTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Nested
    class Search {

        @Test
        @DisplayName("""
                마지막 기사로부터 다음 페이지의 커서를 생성한다.
                orderBy = publishDate
                pageSize = 5
                """)
        void convertArticleCursorPageResponse() {
            // given
            List<Article> articles = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                        articles.add(ArticleFixture.createEntity(
                                ArticleCreateRequestFixture.createDummy(0, -i)
                        ));
            }
            Article article = ArticleFixture.createEntity(ArticleCreateRequestFixture.createDummy(0, -6));
            articles.add(article);
            ArticleDto dto = new ArticleDto(article.getTitle(), article.getSummary(), "NAVER", article.getPublishDate());

            LocalDateTime lastDate = article.getPublishDate();
            LocalDateTime lastCreatedAt = article.getCreatedAt();
            long total = 7;
            int pageSize = 5;

            Pageable pageable = PageRequest.of(0, pageSize);
            Slice<Article> articlePage = new SliceImpl<>(articles, pageable, true);

            when(articleRepository.findByKeywordAndSource(any())).thenReturn(articlePage);
            when(articleRepository.countTotalElements(any())).thenReturn(total);
            when(articleMapper.toDto(any())).thenReturn(dto);

            // when
            ArticleSearchRequest request = ArticleSearchRequestFixture.createWithOrderBy("publishDate", 5);
            PageResponse<ArticleDto> articlePages = articleService.searchByKeyword(request);

            // then
            assertThat(articlePages.nextCursor()).isEqualTo(lastDate.toString());
            assertThat(articlePages.nextAfter()).isEqualTo(lastCreatedAt);

            assertThat(articlePages.size()).isEqualTo(pageSize);
            assertThat(articlePages.totalElements()).isEqualTo(total);
            assertThat(articlePages.hasNext()).isTrue();

            assertThat(articlePages.content()).hasSize(7);
        }
    }
}
