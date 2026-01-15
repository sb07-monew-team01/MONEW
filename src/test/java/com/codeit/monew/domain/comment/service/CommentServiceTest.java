package com.codeit.monew.domain.comment.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.request.CommentUpdateRequest;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.exception.CommentAlreadyDeleteException;
import com.codeit.monew.domain.comment.exception.CommentContentEmptyException;
import com.codeit.monew.domain.comment.exception.CommentContentTooLongException;
import com.codeit.monew.domain.comment.exception.CommentNotFoundException;
import com.codeit.monew.domain.comment.repository.CommentRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

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
    UUID commentId;

    @BeforeEach
    void setUp() {
        articleId = UUID.randomUUID();
        userId = UUID.randomUUID();
        commentId = UUID.randomUUID();
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

    @Nested
    @DisplayName("댓글 삭제")
    class DeleteComment {
        User user = new User("test@email.com","nick","1234");
        Article article = new Article(
                ArticleSource.NAVER,
                "www.naver.com/article/123",
                "제목입니다.",
                LocalDateTime.now(),
                "요약입니다.",
                null
        );
        Comment comment = new Comment(user, article, "삭제 테스트용 댓글");

        @Test
        @DisplayName("성공: 댓글이 정상적으로 논리 삭제된다.")
        void softDeleteComment_success() {
            // given
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

            // when
            commentService.delete(commentId);

            // then
            assertThat(comment.isDeleted()).isTrue();
            assertThat(comment.getDeletedAt()).isNotNull();

        }

        @Test
        @DisplayName("실패: 같은 댓글을 여러 번 삭제할 수 없다.")
        void failToSoftDeleteComment_alreadyDelete() {
            // given
            Comment deletedComment = new Comment(user, article, "이미 삭제된 댓글");
            deletedComment.softDelete();
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(deletedComment));

            assertThatThrownBy(() -> commentService.delete(commentId))
                    .isInstanceOf(CommentAlreadyDeleteException.class);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 댓글 ID로 삭제를 시도할 수 없다.")
        void failToSoftDeleteComment_notFound() {
            // given
            UUID invalidId = UUID.randomUUID();
            when(commentRepository.findById(invalidId)).thenReturn(Optional.empty());

            //when & then
            assertThatThrownBy(()-> commentService.delete(invalidId))
                    .isInstanceOf(CommentNotFoundException.class);
        }

        @Test
        @DisplayName("성공: 댓글이 정상적으로 물리 삭제된다.")
        void hardDeleteComment_success() {
            // given
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

            // when
            commentService.deleteHard(commentId);

            // then
            then(commentRepository).should().delete(comment);
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class UpdateComment {
        User user = new User("test@email.com", "nick", "1234");
        Article article = new Article(
                ArticleSource.NAVER,
                "www.naver.com/article/123",
                "제목입니다.",
                LocalDateTime.now(),
                "요약입니다.",
                null);
        Comment comment = new Comment(user, article, "수정 전 댓글 내용");

        @Test
        @DisplayName("성공: 댓글이 정상적으로 수정된다.")
        void updateComment_success() {
            // given
            CommentUpdateRequest request = new CommentUpdateRequest("수정 후 댓글 내용");
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(comment));

            // when
            commentService.update(commentId, request);

            // then
            assertThat(comment.getContent()).isEqualTo(request.content());
            then(commentRepository).should().findById(commentId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 댓글 ID로 수정을 시도할 수 없다.")
        void failToUpdateComment_notFound() {
            // given
            UUID invalidId = UUID.randomUUID();
            CommentUpdateRequest request = new CommentUpdateRequest("수정 후 댓글 내용");

            given(commentRepository.findById(invalidId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.update(invalidId, request))
                    .isInstanceOf(CommentNotFoundException.class);

        }

        @Test
        @DisplayName("실패: 댓글 내용이 500자를 초과할 경우 댓글 수정에 실패한다.")
        void failToUpdateComment_exceedContent() {
            // given
            String longContent = "a".repeat(501);
            CommentUpdateRequest request = new CommentUpdateRequest(longContent);

            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(comment));

            // when & then
            assertThatThrownBy(() -> commentService.update(commentId, request))
                    .isInstanceOf(CommentContentTooLongException.class);
        }

        @Test
        @DisplayName("실패: 댓글 내용이 없을 경우 예외가 발생한다.")
        void failToUpdateComment_null() {
            // given
            CommentUpdateRequest request = new CommentUpdateRequest(null);

            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(comment));

            // when & then
            assertThatThrownBy(() -> commentService.update(commentId, request))
                    .isInstanceOf(CommentContentEmptyException.class);
        }

    }
}

