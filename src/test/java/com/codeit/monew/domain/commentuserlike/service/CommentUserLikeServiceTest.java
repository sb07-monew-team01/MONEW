package com.codeit.monew.domain.commentuserlike.service;

import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.commentuserlike.repository.CommentUserLikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 좋아요 서비스 테스트")
public class CommentUserLikeServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentUserLikeRepository commentUserLikeRepository;
    @InjectMocks
    private CommentUserLikeService commentUserLikeService;

    private UUID userId;
    private UUID commentId;
    private Comment comment;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        commentId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("댓글 좋아요를 누른다.")
    class LikeComment {

        @DisplayName("성공: 좋아요를 처음 누르면 저장된다.")
        @Test
        void success_likeComment() {
            // given
            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
            given(commentUserLikeRepository.findByUserIdAndCommentId(userId, commentId))
                    .willReturn(Optional.empty());
            // when
            commentUserLikeService.like(userId, commentId);
            // then
            verify(commentUserLikeRepository, times(1)).save(any(CommentUserLike.class);

    }

}
