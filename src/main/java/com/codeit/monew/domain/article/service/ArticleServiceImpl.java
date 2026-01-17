package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.articleView.repository.ArticleViewRepository;
import com.codeit.monew.domain.interestkeyword.InterestKeyword;
import com.codeit.monew.domain.article.exception.ArticleNotFoundException;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.global.dto.PageResponse;
import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.matcher.ArticleMatcher;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.InterestNotFoundException;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.global.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleViewRepository articleViewRepository;
    private final ArticleRepository articleRepository;
    private final InterestRepository interestRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;
    private final ArticleMatcher articleMatcher;

    @Transactional
    @Override
    public void createArticle(ArticleCreateRequest request, List<Interest> interests) {
        if(articleRepository.existsBySourceUrl(request.sourceUrl()))
            return;

        if(!articleMatcher.match(request, interests)){
            return;
        }

        Article article = Article.builder()
                .source(request.source())
                .sourceUrl(request.sourceUrl())
                .title(request.title())
                .publishDate(request.publishDate())
                .summary(request.summary())
                .build();

        articleRepository.save(article);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ArticleDto> searchByKeyword(ArticleSearchRequest request, UUID userId) {

        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        List<String> keywords = new ArrayList<>();
        if (request.keyword() != null) keywords.add(request.keyword());
        if (request.interestId() != null) {
            Interest interest = interestRepository.findById(request.interestId()).orElseThrow(
                    () -> new InterestNotFoundException(ErrorCode.INTEREST_NOT_FOUND, Map.of("interestId", request.interestId())));
            keywords.add(interest.getName());
            keywords.addAll(interest.getKeywords().stream().map(InterestKeyword::getKeyword).toList());
        }

        ArticleSearchCondition condition = ArticleSearchCondition.of(request, keywords);

        Slice<Article> articlePage = articleRepository.findByKeywordsAndSources(condition);
        long total = articleRepository.countTotalElements(condition);

        Set<UUID> viewedArticleIds = Collections.emptySet();
        if (!articlePage.isEmpty()) {
            List<UUID> articleIds = articlePage.stream().map(Article::getId).toList();
            viewedArticleIds = articleViewRepository
                    .findViewedByUserIdAndArticleId(userId, articleIds);
        }


        String nextCursor = null;
        LocalDateTime nextAfter = null;

        if (articlePage.hasNext() && !articlePage.isEmpty()) {
            List<Article> content = articlePage.getContent();
            Article lastArticle = content.get(content.size() - 1);

            String value = switch (condition.orderBy()) {
                case "viewCount" -> String.valueOf(lastArticle.getViewCount());
                case "commentCount" -> String.valueOf(lastArticle.getCommentCount());
                default -> String.valueOf(lastArticle.getPublishDate());
            };

            nextCursor = value + "_" + lastArticle.getId();
            nextAfter = lastArticle.getCreatedAt();
        }

        Set<UUID> viewedArticleIdsSet = new HashSet<>(viewedArticleIds);
        List<ArticleDto> content = articlePage.stream()
                .map(article -> {
                    boolean viewedByMe = viewedArticleIdsSet.contains(article.getId());
                    return articleMapper.toDto(article, viewedByMe);
                })
                .toList();

        return new PageResponse<>(content, nextCursor, nextAfter, condition.limit(), total, articlePage.hasNext());
    }

    @Transactional(readOnly = true)
    @Override
    public ArticleDto searchByUserIdAndArticleId(UUID userId, UUID articleId) {

        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));

        boolean viewedByMe = articleViewRepository.existsByUserIdAndArticleId(userId, articleId);

        return articleMapper.toDto(article, viewedByMe);
    }
}
