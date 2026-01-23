package com.codeit.monew.domain.comment.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.comment.dto.request.*;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.dto.response.CommentWithLikeCount;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.commentuserlike.repository.CommentUserLikeRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.global.dto.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 서비스 테스트")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleRepository articleRepository;

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
    }

    @Test
    @DisplayName("댓글 등록 성공")
    void create_success() {
        User user = new User("test@email.com", "nick", "1234");
        Article article = mock(Article.class);

        CommentRegisterRequest request =
                new CommentRegisterRequest(articleId, userId, "댓글");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        given(commentRepository.save(org.mockito.ArgumentMatchers.any(Comment.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        CommentDto result = commentService.create(request);

        assertThat(result.content()).isEqualTo("댓글");
    }

    @Test
    @DisplayName("댓글 논리 삭제 성공")
    void delete_success() {
        Comment comment = mock(Comment.class);
        Article article = mock(Article.class);

        given(comment.getArticle()).willReturn(article);
        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));

        commentService.delete(commentId);

        then(comment).should().softDelete();
        then(article).should().decreaseCommentCount();
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void update_success() {
        Comment comment = mock(Comment.class);
        Article article = mock(Article.class);
        User user = mock(User.class);

        given(comment.getArticle()).willReturn(article);
        given(comment.getUser()).willReturn(user);
        given(user.getId()).willReturn(userId);
        given(user.getNickname()).willReturn("닉");

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));

        CommentUpdateRequest request = new CommentUpdateRequest("수정");

        commentService.update(commentId, userId, request);

        then(comment).should().updateContent("수정");
    }

    @Test
    @DisplayName("댓글 조회 성공 - 좋아요 포함")
    void read_success_withLike() {
        Comment comment = mock(Comment.class);
        Article article = mock(Article.class);
        User commentUser = mock(User.class);

        given(comment.getId()).willReturn(commentId);
        given(comment.getArticle()).willReturn(article);
        given(article.getId()).willReturn(articleId);
        given(comment.getUser()).willReturn(commentUser);
        given(commentUser.getId()).willReturn(UUID.randomUUID());
        given(commentUser.getNickname()).willReturn("닉");
        given(comment.getContent()).willReturn("댓글");
        given(comment.getCreatedAt()).willReturn(LocalDateTime.now());

        CommentWithLikeCount dto =
                new CommentWithLikeCount(comment, 3L);

        Slice<CommentWithLikeCount> slice =
                new SliceImpl<>(
                        List.of(dto),
                        PageRequest.of(0, 5),
                        false
                );

        given(commentRepository.findByArticleIdOrderBy(
                eq(articleId),
                eq(CommentOrderBy.createdAt),
                eq(null),
                eq(5)
        )).willReturn(slice);

        given(commentUserLikeRepository.existsByUserIdAndCommentId(userId, commentId))
                .willReturn(true);

        CommentSearchRequest request =
                new CommentSearchRequest(
                        articleId,
                        userId,
                        CommentOrderBy.createdAt,
                        SortDirection.DESC,
                        null,
                        null,
                        5
                );

        PageResponse<CommentDto> response =
                commentService.getComments(request);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).likedByMe()).isTrue();
        assertThat(response.content().get(0).likeCount()).isEqualTo(3L);
    }

    @Test
    @DisplayName("댓글 조회 - hasNext true")
    void read_hasNext_true() {
        Comment comment = mock(Comment.class);
        Article article = mock(Article.class);
        User user = mock(User.class);

        given(comment.getId()).willReturn(commentId);
        given(comment.getArticle()).willReturn(article);
        given(article.getId()).willReturn(articleId);
        given(comment.getUser()).willReturn(user);
        given(user.getId()).willReturn(UUID.randomUUID());
        given(user.getNickname()).willReturn("닉");
        given(comment.getContent()).willReturn("댓글");
        given(comment.getCreatedAt()).willReturn(LocalDateTime.now());

        CommentWithLikeCount dto =
                new CommentWithLikeCount(comment, 0L);

        Slice<CommentWithLikeCount> slice =
                new SliceImpl<>(
                        List.of(dto),
                        PageRequest.of(0, 5),
                        true
                );

        given(commentRepository.findByArticleIdOrderBy(
                eq(articleId),
                eq(CommentOrderBy.createdAt),
                eq(null),
                eq(5)
        )).willReturn(slice);

        CommentSearchRequest request =
                new CommentSearchRequest(
                        articleId,
                        null,
                        CommentOrderBy.createdAt,
                        SortDirection.DESC,
                        null,
                        null,
                        5
                );

        PageResponse<CommentDto> response =
                commentService.getComments(request);

        assertThat(response.hasNext()).isTrue();
    }
}
