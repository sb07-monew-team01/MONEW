package com.codeit.monew.domain.article.service;

import com.codeit.monew.common.dto.PageResponse;
import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.matcher.ArticleMatcher;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.interest.entity.Interest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;
    private ArticleMapper articleMapper;
    private ArticleMatcher articleMatcher;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ArticleDto> searchByKeyword(ArticleSearchRequest request) {

        ArticleSearchCondition condition = new ArticleSearchCondition(
                request.keyword(), request.sourceIn(), request.publishDateFrom(),
                request.publishDateTo(), request.orderBy(), request.direction(),
                request.cursor(), request.after(), request.limit()
        );

        Page<Article> articlePage = articleRepository.findByKeywordAndSource(condition);

        long total = articleRepository.countTotalElements(condition);

        Object nextCursor = null;
        LocalDateTime nextAfter = null;

        if (articlePage.hasNext() && !articlePage.isEmpty()) {
            List<Article> content = articlePage.getContent();
            Article lastArticle = content.get(content.size() - 1);

            nextCursor = switch (request.orderBy()) {
//                case "viewCount" -> lastArticle.getViewCount();
//                case "commentCount" -> lastArticle.getCommentCount();
                default -> lastArticle.getPublishDate();
            };
            nextAfter = lastArticle.getCreatedAt();
        }

        List<ArticleDto> content = articlePage.getContent().stream()
                .map(articleMapper::toDto)
                .toList();

        return new PageResponse<>(content, nextCursor, nextAfter, request.limit(), total, articlePage.hasNext());
    }

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
}
