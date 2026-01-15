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
import com.codeit.monew.domain.article.service.ArticleServiceImpl;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceSearchTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private InterestRepository interestRepository;

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

            when(articleRepository.findByKeywordsAndSources(any())).thenReturn(articlePage);
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

        @Test
        @DisplayName("""
                관심사Id를 전달하면 InterestRepository를 조회하고,
                조건에 맞는 기사 목록 조회 후,
                결과를 dto로 변환하다.
                """)
        void searchAllByInterestId() {
            // given
            UUID interestId = UUID.randomUUID();
            Interest interest = new Interest("음식", List.of("냉면", "라면"));
            when(interestRepository.findById(interestId)).thenReturn(Optional.of(interest));

            Article a1 = ArticleFixture.createEntity(
                    ArticleCreateRequestFixture.createWithTitleAndSummary("음식", "냉면, 라면"));
            ArticleDto dto = new ArticleDto(a1.getTitle(), a1.getSummary(), a1.getSourceUrl(), a1.getPublishDate());

            Slice<Article> articlePage = new SliceImpl<>(List.of(a1), Pageable.unpaged(), true);

            when(articleRepository.findByKeywordsAndSources(any())).thenReturn(articlePage);
            when(articleMapper.toDto(a1)).thenReturn(dto);

            // when
            ArticleSearchRequest request = ArticleSearchRequestFixture.createWithInterestId(interestId);
            PageResponse<ArticleDto> result = articleService.searchByKeyword(request);

            // then
            verify(interestRepository, times(1)).findById(interestId);
            verify(articleRepository, times(1)).findByKeywordsAndSources(any());
            verify(articleMapper, times(1)).toDto(a1);

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0)).isEqualTo(dto);
        }
    }
}
