package com.codeit.monew.domain.comment.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.user.User;
import com.codeit.monew.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Override
    public CommentDto createComment(CommentRegisterRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Article article = articleRepository.findById(request.articleId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기사입니다."));

        Comment comment = new Comment(user, article, request.content());
        Comment saved = commentRepository.save(comment);

        return new CommentDto(
                saved.getId(),
                saved.getArticle().getId(),
                saved.getUser().getId(),
                saved.getUser().getNickname(),
                saved.getContent(),
                0L,
                false,
                saved.getCreatedAt()
        );
    }
}
