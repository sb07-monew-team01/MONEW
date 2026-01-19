package com.codeit.monew.domain.articleView.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.exception.ArticleNotFoundException;
import com.codeit.monew.domain.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.domain.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.articleView.dto.mapper.ArticleViewMapper;
import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;
import com.codeit.monew.domain.articleView.entity.ArticleView;
import com.codeit.monew.domain.articleView.fixture.ArticleViewDtoFixture;
import com.codeit.monew.domain.articleView.repository.ArticleViewRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.global.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleViewServiceImplTest {

    @Mock
    private ArticleViewRepository articleViewRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ArticleViewMapper articleViewMapper;

    @InjectMocks
    private ArticleViewServiceImpl articleViewService;

    UUID articleId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    Article article = ArticleFixture.createWithViewAndComment(
            ArticleCreateRequestFixture.createDefault(), 0, 0
    );
    User user = new User("email@a.a", "nickname", "password");

    @Test
    @DisplayName("기사id와 유저id로 기사 뷰를 생성한다.")
     void createArticleViewByArticleIdAndUserId_Success() {
        // given

        ArticleView articleView = new ArticleView(user, article);
        ArticleViewDto dto = ArticleViewDtoFixture.createDtoEntity(articleView);

        when(articleViewRepository.existsByUserIdAndArticleId(userId, articleId)).thenReturn(false);
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(articleViewMapper.toDto(any(ArticleView.class))).thenReturn(dto);

        // when
        ArticleViewDto response = articleViewService.createArticleView(articleId, userId);

        // then
        verify(articleRepository, times(1)).findById(articleId);
        verify(userRepository, times(1)).findById(userId);
        verify(articleViewMapper, times(1)).toDto(any(ArticleView.class));

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("기사 뷰가 이미 있다면 만들지 않는다.")
    void createArticleView_AlreadyExists() {
        // given

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleViewRepository.existsByUserIdAndArticleId(userId, articleId)).thenReturn(true);

        // when
        ArticleViewDto response = articleViewService.createArticleView(articleId, userId);

        // then
        verify(articleViewRepository, times(0)).save(any(ArticleView.class));
        verify(articleViewMapper, times(0)).toDto(any(ArticleView.class));
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("사용자id가 존재하지 않으면 예외를 반환한다.")
    void createArticleView_ThrowException_WhenNotFoundUser() {
        // given

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> articleViewService.createArticleView(articleId, userId))
                .isInstanceOf(UserNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("기사id가 존재하지 않으면 예외를 반환한다.")
    void createArticleView_ThrowException_WhenNotFoundArticle() {
        // given

        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> articleViewService.createArticleView(articleId, userId))
                .isInstanceOf(ArticleNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ARTICLE_NOT_FOUND);
    }
}