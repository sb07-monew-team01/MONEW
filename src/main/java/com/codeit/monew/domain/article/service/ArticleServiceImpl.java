package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.interestkeyword.entity.InterestKeyword;
import com.codeit.monew.domain.article.exception.ArticleNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;
    private InterestRepository interestRepository;
    private ArticleMapper articleMapper;
    private ArticleMatcher articleMatcher;

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
    public PageResponse<ArticleDto> searchByKeyword(ArticleSearchRequest request) {

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

        List<ArticleDto> content = articlePage.getContent().stream()
                .map(articleMapper::toDto)
                .toList();

        return new PageResponse<>(content, nextCursor, nextAfter, condition.limit(), total, articlePage.hasNext());
    }

    @Transactional(readOnly = true)
    @Override
    public ArticleDto searchById(UUID articleId) {

        return articleRepository.findById(articleId)
                .map(articleMapper::toDto)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
    }
}
