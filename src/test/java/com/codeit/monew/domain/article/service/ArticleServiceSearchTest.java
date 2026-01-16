package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.exception.ArticleNotFoundException;
import com.codeit.monew.domain.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.domain.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.fixture.ArticleSearchRequestFixture;
import com.codeit.monew.domain.articleView.entity.ArticleView;
import com.codeit.monew.domain.articleView.repository.ArticleViewRepository;
import com.codeit.monew.domain.interest.exception.InterestNotFoundException;
import com.codeit.monew.domain.interest.exception.KeywordValidException;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.global.dto.PageResponse;
import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.global.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceSearchTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private InterestRepository interestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ArticleViewRepository articleViewRepository;

    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Nested
    class Search {

        @Nested
        @DisplayName("검색 조건을 통해 기사 목록을 조회한다.")
        class SearchArticlePages{

            @ParameterizedTest
            @CsvSource({
                    "publishDate",
                    "commentCount",
                    "viewCount"
            })
            @DisplayName("""
                마지막 기사로부터 다음 페이지의 커서를 생성한다.
                orderBy = publishDate, commentCount, viewCount
                pageSize = 5
                """)
            void convertArticleCursorByOrderBy(String orderBy) {
                // given
                List<Article> articles = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    articles.add(ArticleFixture.createWithViewAndComment(
                            ArticleCreateRequestFixture.createDummy(0, -i), 20-i, 10-i
                    ));
                }

                Article article = ArticleFixture.createEntity(ArticleCreateRequestFixture.createDummy(0, -6));
                articles.add(article);
                ArticleDto dto = new ArticleDto(article.getTitle(), article.getSummary(), "NAVER", article.getPublishDate());

                LocalDateTime lastCreatedAt = article.getCreatedAt();
                long total = 7;
                int pageSize = 5;

                Pageable pageable = PageRequest.of(0, pageSize);
                Slice<Article> articlePage = new SliceImpl<>(articles, pageable, true);

                when(articleRepository.findByKeywordsAndSources(any())).thenReturn(articlePage);
                when(articleRepository.countTotalElements(any())).thenReturn(total);
                when(articleMapper.toDto(any())).thenReturn(dto);

                // when
                ArticleSearchRequest request = ArticleSearchRequestFixture.createWithOrderBy(orderBy, 5);
                PageResponse<ArticleDto> articlePages = articleService.searchByKeyword(request);

                // then
                String expectedCursor = String.valueOf(switch (orderBy) {
                    case "viewCount" -> article.getViewCount() + "_" + article.getId();
                    case "commentCount" -> article.getCommentCount() + "_" + article.getId();
                    default -> article.getPublishDate() + "_" + article.getId();
                });

                assertThat(articlePages.nextCursor()).isEqualTo(expectedCursor);
                assertThat(articlePages.nextAfter()).isEqualTo(lastCreatedAt);

                assertThat(articlePages.size()).isEqualTo(pageSize);
                assertThat(articlePages.totalElements()).isEqualTo(total);
                assertThat(articlePages.hasNext()).isTrue();

                assertThat(articlePages.content()).hasSize(7);
            }

            @Test
            @DisplayName("검색어에 맞는 기사 목록을 조회 후, 결과를 dto로 반환한다.")
            void searchAllByKeyword() {
                // given
                Article a1 = ArticleFixture.createEntity(
                        ArticleCreateRequestFixture.createWithTitleAndSummary("안녕", "hello world!"));
                ArticleDto dto = new ArticleDto(a1.getTitle(), a1.getSummary(), a1.getSourceUrl(), a1.getPublishDate());

                Slice<Article> articlePage = new SliceImpl<>(List.of(a1), Pageable.unpaged(), true);

                when(articleRepository.findByKeywordsAndSources(any())).thenReturn(articlePage);
                when(articleMapper.toDto(a1)).thenReturn(dto);

                // when
                ArticleSearchRequest request = ArticleSearchRequestFixture.createWithKeyword("안녕");
                PageResponse<ArticleDto> result = articleService.searchByKeyword(request);

                // then
                verify(articleRepository, times(1)).findByKeywordsAndSources(any());
                verify(articleMapper, times(1)).toDto(a1);

                assertThat(result.content()).hasSize(1);
                assertThat(result.content().get(0)).isEqualTo(dto);
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
            
            @Test
            @DisplayName("마지막 페이 조회 후, 페이지dto를 반환하지 않는다.")
            void searchLastPage_ReturnNoNextPage() {
                // given
                Article a1 = ArticleFixture.createEntity(
                        ArticleCreateRequestFixture.createWithTitleAndSummary("안녕", "hello world!"));
                ArticleDto dto = new ArticleDto(a1.getTitle(), a1.getSummary(), a1.getSourceUrl(), a1.getPublishDate());

                Pageable pageable = PageRequest.of(0, 5);
                Slice<Article> articlePage = new SliceImpl<>(List.of(a1), pageable, false);

                when(articleRepository.findByKeywordsAndSources(any())).thenReturn(articlePage);
                when(articleMapper.toDto(a1)).thenReturn(dto);
                when(articleRepository.countTotalElements(any())).thenReturn(1L);

                // when
                ArticleSearchRequest request = ArticleSearchRequestFixture.createWithKeyword("안녕");
                PageResponse<ArticleDto> result = articleService.searchByKeyword(request);

                // then
                verify(articleRepository, times(1)).findByKeywordsAndSources(any());
                verify(articleMapper, times(1)).toDto(a1);

                assertThat(result.content()).hasSize(1);
                assertThat(result.hasNext()).isFalse();
                assertThat(result.nextCursor()).isNull();
                assertThat(result.nextAfter()).isNull();
            }
            
            @Test
            @DisplayName("검색 결과가 없으면 nextCursor와 nextAfter가 null을 반환한다.")
            void searchEmptyResult_ReturnNullCursorAndAfter() {
                // given
                Slice<Article> articlePage = new SliceImpl<>(List.of(), Pageable.unpaged(), true);

                when(articleRepository.findByKeywordsAndSources(any())).thenReturn(articlePage);
                when(articleRepository.countTotalElements(any())).thenReturn(0L);

                // when
                ArticleSearchRequest request = ArticleSearchRequestFixture.createWithKeyword("안녕");
                PageResponse<ArticleDto> result = articleService.searchByKeyword(request);
                
                // then
                verify(articleRepository, times(1)).findByKeywordsAndSources(any());

                assertThat(result.content()).isEmpty();
                assertThat(result.nextCursor()).isNull();
                assertThat(result.nextAfter()).isNull();
            }

            @Test
            @DisplayName("잘못된 관심사id를 받으면 예외를 반환한다.")
            void searchAllByInterestId_ThrowException_WhenNotFound() {
                // given
                UUID interestId = UUID.randomUUID();
                ArticleSearchRequest request = ArticleSearchRequestFixture.createWithInterestId(interestId);
                when(interestRepository.findById(interestId)).thenReturn(Optional.empty());
                // when & then
                assertThatThrownBy(() -> articleService.searchByKeyword(request))
                        .isInstanceOf(InterestNotFoundException.class)
                        .extracting("errorCode")
                        .isEqualTo(ErrorCode.INTEREST_NOT_FOUND);

                verify(interestRepository, times(1)).findById(request.interestId());
            }
        }

        @Nested
        @DisplayName("단일 기사를 조회한다.")
        class SearchArticle{

            @Test
            @DisplayName("""
                    기사 id를 통해 조회한다.
                    처음 조회라면 기사 뷰를 생성하고 조회수를 하나 올린다.
                    """)
            void searchArticleByIdAndUserId() {
                // given
                UUID userId = UUID.randomUUID();
                UUID articleId = UUID.randomUUID();

                Article article = ArticleFixture.createWithViewAndComment(
                        ArticleCreateRequestFixture.createDefault(), 5, 5);
                User user = new User("email@a.a", "nickname", "password");

                when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
                when(userRepository.findById(userId)).thenReturn(Optional.of(user));
                when(articleViewRepository.existsByUserIdAndArticleId(userId, articleId)).thenReturn(false);

                // when
                ArticleDto dto = articleService.searchById(articleId);

                // then
                verify(articleRepository, times(1)).findById(articleId);
                verify(userRepository, times(1)).findById(userId);
                verify(articleMapper, times(1)).toDto(article);

                verify(articleViewRepository, times(1)).save(any(ArticleView.class));

                assertThat(dto).isNotNull();
                assertThat(dto.viewCount()).isEqualTo(6);
            }
        }
        @Test
        @DisplayName("잘못된 기사id를 받으면 예외를 반환한다.")
        void searchArticle_ThrowException_WhenNotFound() {
            // given
            UUID articleId = UUID.randomUUID();

            when(articleRepository.findById(articleId)).thenReturn(Optional.empty());
            // when & then
            assertThatThrownBy(() -> articleService.searchById(articleId))
                    .isInstanceOf(ArticleNotFoundException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.ARTICLE_NOT_FOUND);

            verify(articleRepository, times(1)).findById(articleId);
        }
    }
}
