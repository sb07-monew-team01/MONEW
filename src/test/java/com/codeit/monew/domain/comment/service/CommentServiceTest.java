package com.codeit.monew.domain.comment.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.exception.ArticleNotFoundException;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.comment.dto.request.*;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.exception.CommentAlreadyDeleteException;
import com.codeit.monew.domain.comment.exception.CommentNotFoundException;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.commentuserlike.repository.CommentUserLikeRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.global.dto.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
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
    @Mock
    private CommentUserLikeRepository commentUserLikeRepository;

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

        Slice<CommentWithLikeCount> slice =
                new SliceImpl<>(
                        List.of(),                 // 또는 테스트용 데이터
                        PageRequest.of(0, 10),
                        false
                );
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
        @DisplayName("실패: 존재하지 않는 기사 ID로 댓글 생성 시 예외 발생")
        void failToCreateComment_articleNotFound() {
            // given
            User user = new User("test@email.com", "nick", "1234");
            UUID notExistArticleId = UUID.randomUUID();
            String content = "test";

            CommentRegisterRequest request =
                    new CommentRegisterRequest(notExistArticleId, userId, content);

            given(userRepository.findById(userId))
                    .willReturn(Optional.of(user));

            given(articleRepository.findById(notExistArticleId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.create(request))
                    .isInstanceOf(ArticleNotFoundException.class);
        }

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
            commentService.update(commentId, userId, request);

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
            assertThatThrownBy(() -> commentService.update(invalidId, userId, request))
                    .isInstanceOf(CommentNotFoundException.class);

        }

    }
    @Nested
    @DisplayName("댓글 조회")
    class ReadComment {
        @Test
        @DisplayName("성공: 댓글 조회 시 좋아요 정보가 포함된다.")
        void readComment_includeLikeInfo() {
            // given
            Comment comment = mock(Comment.class);
            given(comment.getId()).willReturn(UUID.randomUUID());
            given(comment.getContent()).willReturn("댓글");
            given(comment.getCreatedAt()).willReturn(LocalDateTime.now());
            given(comment.getArticle()).willReturn(mock(Article.class));
            given(comment.getUser()).willReturn(mock(User.class));
            given(comment.getUser().getId()).willReturn(UUID.randomUUID());
            given(comment.getUser().getNickname()).willReturn("nick");

            CommentWithLikeCount cwlc = new CommentWithLikeCount(comment, 3L);

            given(commentRepository.findByArticleIdOrderBy(
                    any(UUID.class),
                    any(CommentOrderBy.class),
                    any(SortDirection.class),
                    any(),
                    any(),
                    anyInt()
            )).willReturn(new SliceImpl<>(List.of(cwlc), PageRequest.of(0, 10), false));


            given(commentUserLikeRepository.existsByUserIdAndCommentId(any(), any()))
                    .willReturn(true);


            // when
            CommentSearchRequest request =
                    new CommentSearchRequest(
                            articleId,
                            userId,
                            CommentOrderBy.createdAt,
                            SortDirection.DESC,
                            null,
                            null,
                            20
                    );

            PageResponse<CommentDto> response =
                    commentService.getComments(request);


            // then
            CommentDto dto = response.content().get(0);
            assertThat(dto.likeCount()).isEqualTo(3L);
            assertThat(dto.likedByMe()).isTrue();

        }

        @Test
        @DisplayName("성공: 댓글 조회 시 좋아요가 없으면 likeCount=0, likedByMe=false로 반환된다.")
        void readComment_noLikes() {
            // given
            Comment comment = mock(Comment.class);
            Article article = mock(Article.class);
            User user = mock(User.class);

            given(comment.getId()).willReturn(commentId);
            given(comment.getArticle()).willReturn(article);
            given(comment.getUser()).willReturn(user);
            given(comment.getContent()).willReturn("테스트 댓글");
            given(comment.getCreatedAt()).willReturn(LocalDateTime.now());

            given(article.getId()).willReturn(articleId);
            given(user.getId()).willReturn(userId);
            given(user.getNickname()).willReturn("닉네임");

            CommentWithLikeCount projection = mock(CommentWithLikeCount.class);
            given(projection.comment()).willReturn(comment);
            given(projection.likeCount()).willReturn(0L);

            Slice<CommentWithLikeCount> slice =
                    new SliceImpl<>(List.of(projection), Pageable.unpaged(), false);

            given(commentRepository.findByArticleIdOrderBy(
                    eq(articleId),
                    eq(CommentOrderBy.createdAt),
                    eq(SortDirection.DESC),
                    isNull(),
                    isNull(),
                    eq(10)
            )).willReturn(slice);


            given(commentUserLikeRepository.existsByUserIdAndCommentId(userId, commentId))
                    .willReturn(false);

            // when
            CommentSearchRequest request =
                    new CommentSearchRequest(
                            articleId,
                            userId,
                            CommentOrderBy.createdAt,
                            SortDirection.DESC,
                            null,
                            null,
                            10
                    );

            PageResponse<CommentDto> response =
                    commentService.getComments(request);


            // then
            assertThat(response.content()).hasSize(1);

            CommentDto dto = response.content().get(0);
            assertThat(dto.likeCount()).isEqualTo(0L);
            assertThat(dto.likedByMe()).isFalse();
            assertThat(dto.content()).isEqualTo("테스트 댓글");
        }

        @Test
        @DisplayName("성공: 댓글이 없는 경우 빈 목록과 hasNext=false 반환")
        void readComment_noComments() {
            // given
            Slice<CommentWithLikeCount> emptySlice =
                    new SliceImpl<>(List.of(), Pageable.unpaged(), false);

            given(commentRepository.findByArticleIdOrderBy(
                    eq(articleId),
                    eq(CommentOrderBy.createdAt),
                    eq(SortDirection.DESC),
                    isNull(),
                    isNull(),
                    eq(10)
            )).willReturn(emptySlice);

            // when
            CommentSearchRequest request =
                    new CommentSearchRequest(
                            articleId,
                            userId,
                            CommentOrderBy.createdAt,
                            SortDirection.DESC,
                            null,
                            null,
                            10
                    );

            PageResponse response =
                    commentService.getComments(request);


            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
            assertThat(response.nextAfter()).isNull();
        }

        @Test
        @DisplayName("성공: 댓글 조회 시 다음 페이지가 있으면 hasNext가 true다")
        void readComment_hasNext_true() {
            // given
            Comment comment = mock(Comment.class);
            Article article = mock(Article.class);

            given(comment.getArticle()).willReturn(article);
            given(article.getId()).willReturn(articleId);

            User user = mock(User.class);

            given(comment.getId()).willReturn(commentId);
            given(comment.getContent()).willReturn("댓글");
            given(comment.getCreatedAt()).willReturn(LocalDateTime.now());
            given(comment.getUser()).willReturn(user);

            given(user.getId()).willReturn(userId);
            given(user.getNickname()).willReturn("닉넴");

            CommentWithLikeCount dto =
                    new CommentWithLikeCount(comment, 0L);


            Slice<CommentWithLikeCount> slice =
                    new SliceImpl<>(
                            List.of(dto),
                            PageRequest.of(0, 10),
                            true
                    );

            given(commentRepository.findByArticleIdOrderBy(
                    eq(articleId),
                    eq(CommentOrderBy.createdAt),
                    eq(SortDirection.DESC),
                    isNull(),
                    isNull(),
                    eq(10)
            )).willReturn(slice);

            // when
            CommentSearchRequest request =
                    new CommentSearchRequest(
                            articleId,
                            userId,
                            CommentOrderBy.createdAt,
                            SortDirection.DESC,
                            null,
                            null,
                            10
                    );

            PageResponse response =
                    commentService.getComments(request);


            // then
            assertThat(response.hasNext()).isTrue();
            assertThat(response.content()).hasSize(1);
        }

        @Test
        @DisplayName("성공: 마지막 페이지면 hasNext가 false다")
        void readComment_hasNext_false() {
            // given
            Comment comment = mock(Comment.class);
            User user = mock(User.class);
            Article article = mock(Article.class);

            given(comment.getId()).willReturn(commentId);
            given(comment.getContent()).willReturn("댓글");
            given(comment.getCreatedAt()).willReturn(LocalDateTime.now());
            given(comment.getUser()).willReturn(user);
            given(comment.getArticle()).willReturn(article);
            given(article.getId()).willReturn(articleId);

            given(user.getId()).willReturn(userId);
            given(user.getNickname()).willReturn("닉넴");


            CommentWithLikeCount dto =
                    new CommentWithLikeCount(comment, 0L);


            Slice<CommentWithLikeCount> slice =
                    new SliceImpl<>(
                            List.of(dto),
                            PageRequest.of(0, 10),
                            false
                    );

            given(commentRepository.findByArticleIdOrderBy(
                    eq(articleId),
                    eq(CommentOrderBy.createdAt),
                    eq(SortDirection.DESC),
                    isNull(),
                    isNull(),
                    eq(10)
            )).willReturn(slice);

            // when
            CommentSearchRequest request =
                    new CommentSearchRequest(
                            articleId,
                            userId,
                            CommentOrderBy.createdAt,
                            SortDirection.DESC,
                            null,
                            null,
                            10
                    );

            PageResponse response =
                    commentService.getComments(request);


            // then
            assertThat(response.hasNext()).isFalse();
            assertThat(response.content().size()).isEqualTo(1);
            assertThat(response.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("성공: cursor와 after가 있으면 다음 페이지를 조회한다")
        void readComment_withCursor() {
            // given
            String cursor = commentId.toString();
            LocalDateTime after = LocalDateTime.now().minusMinutes(10);

            Comment comment = mock(Comment.class);
            User user = mock(User.class);
            Article article = mock(Article.class);
            given(article.getId()).willReturn(articleId);
            given(comment.getArticle()).willReturn(article);

            given(comment.getId()).willReturn(commentId);
            given(comment.getContent()).willReturn("댓글");
            given(comment.getCreatedAt()).willReturn(LocalDateTime.now());
            given(comment.getUser()).willReturn(user);

            given(user.getId()).willReturn(userId);
            given(user.getNickname()).willReturn("닉넴");

            CommentWithLikeCount dto =
                    new CommentWithLikeCount(comment, 0L);

            Slice<CommentWithLikeCount> slice =
                    new SliceImpl<>(
                            List.of(dto),
                            PageRequest.of(0, 10),
                            false
                    );

            given(commentRepository.findByArticleIdOrderBy(
                    eq(articleId),
                    eq(CommentOrderBy.createdAt),
                    eq(SortDirection.DESC),
                    eq(cursor),
                    eq(after),
                    eq(10)
            )).willReturn(slice);

            // when
            CommentSearchRequest request =
                    new CommentSearchRequest(
                            articleId,
                            userId,
                            CommentOrderBy.createdAt,
                            SortDirection.DESC,
                            cursor,
                            after,
                            10
                    );

            PageResponse response =
                    commentService.getComments(request);


            // then
            assertThat(response.content().size()).isEqualTo(1);

        }


    }
}

