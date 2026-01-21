package com.codeit.monew.domain.comment.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.exception.ArticleNotFoundException;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.comment.dto.request.*;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.dto.response.CommentPageResponse;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.exception.CommentAlreadyDeleteException;
import com.codeit.monew.domain.comment.exception.CommentNotFoundException;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.commentuserlike.repository.CommentUserLikeRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.global.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codeit.monew.domain.comment.mapper.CommentMapper;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentUserLikeRepository commentUserLikeRepository;
    private final ArticleRepository articleRepository;

    @Override
    @Transactional
    public CommentDto create(CommentRegisterRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        UUID articleId = request.articleId();
        if (articleId == null) {
            throw new IllegalArgumentException("게시글 ID는 필수입니다.");
        }
        Article article = articleRepository.findById(request.articleId())
                .orElseThrow(() -> new ArticleNotFoundException(request.articleId()));

        Comment comment = new Comment(user, article, request.content());
        Comment saved = commentRepository.save(comment);

        article.increaseCommentCount();
        return CommentMapper.toDto(saved);
    }

    // 논리 삭제
    @Override
    @Transactional
    public void delete(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.isDeleted()) {
            throw new CommentAlreadyDeleteException(ErrorCode.COMMENT_ALREADY_DELETE);
        }

        comment.softDelete();
        comment.getArticle().decreaseCommentCount();
    }

    // 물리 삭제
    @Override
    @Transactional
    public void deleteHard(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        commentRepository.delete(comment);
        comment.getArticle().decreaseCommentCount();
    }

    // 수정
    @Override
    @Transactional
    public CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        comment.updateContent(request.content());

        return CommentMapper.toDto(comment);


    }

    @Transactional
    @Override
    public CommentPageResponse getComments(CommentSearchRequest request) {

        UUID articleId = request.articleId();
        UUID userId = request.userId();
        CommentOrderBy orderBy = request.orderBy();
        SortDirection direction = request.direction();
        String cursor = request.cursor();
        LocalDateTime after = request.after();
        int limit = request.limit();

        Slice<CommentWithLikeCount> slice =
                commentRepository.findByCommentIdOrderBy(
                        articleId,
                        orderBy,
                        direction,
                        cursor,
                        after,
                        limit
                );

        List<CommentDto> content = slice.getContent().stream()
                .map(it -> {
                    Comment comment = it.comment();
                    long likeCount = it.likeCount();

                    boolean likedByMe =
                            userId != null &&
                                    commentUserLikeRepository
                                            .existsByUserIdAndCommentId(
                                                    userId,
                                                    comment.getId()
                                            );

                    return CommentMapper.toDto(
                            comment,
                            likeCount,
                            likedByMe
                    );
                })
                .toList();

        String nextCursor = null;
        LocalDateTime nextAfter = null;

        if (slice.hasNext() && !content.isEmpty()) {
            Comment last = slice.getContent()
                    .get(slice.getContent().size() - 1)
                    .comment();

            nextCursor = last.getId().toString();
            nextAfter = last.getCreatedAt();
        }

        return new CommentPageResponse(
                content,
                nextCursor,
                nextAfter,
                limit,
                0L,               // Slice 기반이므로 total 없음
                slice.hasNext()
        );
    }

}