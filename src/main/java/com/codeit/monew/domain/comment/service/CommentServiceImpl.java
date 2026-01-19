package com.codeit.monew.domain.comment.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.request.CommentUpdateRequest;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.dto.response.CommentPageResponse;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.exception.CommentAlreadyDeleteException;
import com.codeit.monew.domain.comment.exception.CommentContentEmptyException;
import com.codeit.monew.domain.comment.exception.CommentContentTooLongException;
import com.codeit.monew.domain.comment.exception.CommentNotFoundException;
import com.codeit.monew.domain.comment.mapper.CommentMapper;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.commentuserlike.repository.CommentUserLikeRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.global.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.codeit.monew.domain.comment.mapper.CommentMapper.toDto;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentUserLikeRepository commentUserLikeRepository;
    private final ArticleRepository articleRepository;

    @Override
    public CommentDto create(CommentRegisterRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        Article article = articleRepository.findById(request.articleId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기사입니다.")); // 해당 도메인에 예외 추가되면 수정 예정

        Comment comment = new Comment(user, article, request.content());
        Comment saved = commentRepository.save(comment);

        article.increaseCommentCount();
        return toDto(saved);
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
    public CommentDto update(UUID commentId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        comment.updateContent(request.content());

        return toDto(comment);


    }

    @Override
    public CommentPageResponse getComments(
            UUID articleId,
            UUID userId,
            Pageable pageable
    ) {
        // 댓글 페이지 조회
        Page<Comment> commentPage = commentRepository.findByArticleId(articleId, pageable);

        // 댓글을 CommentDto로 변환(좋아요 정보를 포함한)
        List<CommentDto> content = commentPage.getContent().stream()
                .map(comment -> {
                    long likeCount =
                            commentUserLikeRepository.countByCommentId(comment.getId());

                    boolean likedByMe = false;
                    if (userId != null) {
                        likedByMe =
                                commentUserLikeRepository.existsByUserIdAndCommentId(userId, comment.getId());
                    }
                    return new CommentDto(
                            comment.getId(),
                            comment.getArticle().getId(),
                            comment.getUser().getId(),
                            comment.getUser().getNickname(),
                            comment.getContent(),
                            likeCount,
                            likedByMe,
                            comment.getCreatedAt()
                    );
                })
                .toList();

        // PageResponse 생성 및 반환
        return new CommentPageResponse(
                content,
                null,
                null,
                pageable.getPageSize(),
                commentPage.getTotalElements(),
                commentPage.hasNext()
        );

    }

}
