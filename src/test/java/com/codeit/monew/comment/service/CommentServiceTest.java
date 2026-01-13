package com.codeit.monew.comment.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.exception.CommentContentEmptyException;
import com.codeit.monew.domain.comment.exception.CommentContentTooLongException;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.comment.service.CommentServiceImpl;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 서비스 테스트")
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    UUID articleId;
    UUID userId;

    @BeforeEach
    void setUp() {
        articleId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("댓글 등록")
    class CreateComment {

        @Test
        @DisplayName("성공: 유저와 기사가 있으면 댓글이 정상적으로 등록된다.")
        void createComment_success() {
            // given
            String content = "test";
            UUID articleId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            User user = new User("test@email.com","nick","1234");
            Article article = new Article(
                    ArticleSource.NAVER,
                    "www.naver.com/article/123",
                    "제목입니다.",
                    LocalDateTime.now(),
                    "요약입니다.",
                    null
            );

            CommentRegisterRequest request = new CommentRegisterRequest(articleId, userId, content);

            Comment saved = new Comment(user, article, content);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
            when(commentRepository.save(any(Comment.class))).thenReturn(saved);

            // when
            CommentDto response = commentService.create(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEqualTo(content);

            verify(userRepository).findById(userId);
            verify(articleRepository).findById(articleId);
            verify(commentRepository).save(any(Comment.class));
        }

        @Test
        @DisplayName("실패: 댓글 내용이 없을 경우 예외가 발생한다.")
        void failToCreateComment_null() {
            // given
            CommentRegisterRequest request = new CommentRegisterRequest(articleId, userId, null);

            // when & then
            assertThatThrownBy(
                    () -> commentService.create(request))
                    .isInstanceOf(CommentContentEmptyException.class);
        }

//        @Test
//        @DisplayName("실패: 기사가 존재하지 않을 경우 예외가 발생한다.")
//        void failToCreateComment_nullArticle() {
//            // given
//            CommentRegisterRequest request = new CommentRegisterRequest(null, userId, content);
//
//            // when & then
//            assertThatThrownBy(
//                    () -> commentService.create(request))
//                    .isInstanceOf(ArticleNotFoundException.class);
//        }

        @Test
        @DisplayName("실패: 사용자가 존재하지 않을 경우 예외가 발생한다.")
        void failToCreateComment_nullUser() {
            // given
            CommentRegisterRequest request = new CommentRegisterRequest(articleId, null, "댓글 내용");

            // when & then
            assertThatThrownBy(
                    () -> commentService.create(request))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("실패: 댓글 내용이 500자를 초과할 경우 댓글 등록에 실패한다.")
        void failToCreateComment_exceedContent() {
            // given
            String longContent1 = "a".repeat(501);
            CommentRegisterRequest request = new CommentRegisterRequest(articleId, userId, longContent1);

            // when & then
            assertThatThrownBy(
                    () -> commentService.create(request))
                    .isInstanceOf(CommentContentTooLongException.class);
        }
    }

}

