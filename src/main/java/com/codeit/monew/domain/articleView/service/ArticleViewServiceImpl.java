package com.codeit.monew.domain.articleView.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.exception.ArticleNotFoundException;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.articleView.dto.mapper.ArticleViewMapper;
import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;
import com.codeit.monew.domain.articleView.entity.ArticleView;
import com.codeit.monew.domain.articleView.repository.ArticleViewRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.domain.userActivity.event.dto.ArticleViewedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleViewServiceImpl implements ArticleViewService {

    private final ArticleViewRepository articleViewRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticleViewMapper articleViewMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @Override
    public ArticleViewDto createArticleView(UUID articleId, UUID userId) {

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (articleViewRepository.existsByUserIdAndArticleId(userId, articleId)) {
            return null;
        }

        ArticleView articleView = new ArticleView(user, article);
        article.increaseViewCount();
        articleViewRepository.save(articleView);

        // 이벤트 발행
        publisher.publishEvent(new ArticleViewedEvent(
                userId,
                articleView
        ));

        return articleViewMapper.toDto(articleView);
    }
}
