package com.codeit.monew.domain.commentuserlike.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.commentuserlike.dto.CommentUserLikeDto;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.commentuserlike.mapper.CommentUserLikeMapper;
import com.codeit.monew.domain.commentuserlike.repository.CommentUserLikeRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 좋아요 서비스 테스트")
class CommentUserLikeServiceTest {

    @Mock
    private CommentUserLikeRepository commentUserLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentUserLikeMapper commentUserLikeMapper;

    @InjectMocks
    private CommentUserLikeServiceImpl commentUserLikeService;

    private UUID userId;
    private UUID commentId;
    private User user;
    private Comment comment;
    private Article article;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        commentId = UUID.randomUUID();

        user = new User("test@email.com", "nick", "1234");
        comment = mock(Comment.class);
    }


    @Nested
    @DisplayName("댓글 좋아요를 누른다")
    class LikeComment {
        @Test
        @DisplayName("성공: 처음 좋아요를 누르면 저장된다")
        void success_like_first_time() {
            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
            given(comment.getId()).willReturn(commentId);
            given(commentUserLikeRepository.findByUserIdAndCommentId(userId, commentId))
                    .willReturn(Optional.empty());
            given(commentUserLikeRepository.countByCommentId(commentId))
                    .willReturn(1L);

            try (MockedStatic<CommentUserLikeMapper> mocked =
                         mockStatic(CommentUserLikeMapper.class)) {

                mocked.when(() ->
                        CommentUserLikeMapper.toDto(any(CommentUserLike.class), anyLong())
                ).thenReturn(mock(CommentUserLikeDto.class));

                // when
                commentUserLikeService.like(userId, commentId);

                // then
                verify(commentUserLikeRepository).save(any(CommentUserLike.class));
            }
        }
        @Test
        @DisplayName("성공: 이미 좋아요 상태에서 다시 누르면 좋아요가 취소된다")
        void success_unlike() {
            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
            CommentUserLike existingLike = mock(CommentUserLike.class);
            given(commentUserLikeRepository.findByUserIdAndCommentId(userId, commentId))
                    .willReturn(Optional.of(existingLike));

            // when
            commentUserLikeService.like(userId, commentId);

            // then
            verify(commentUserLikeRepository).delete(existingLike);
            verify(commentUserLikeRepository, never()).save(any(CommentUserLike.class));
        }


    }
}