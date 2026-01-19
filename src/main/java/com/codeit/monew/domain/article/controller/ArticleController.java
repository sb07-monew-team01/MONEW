package com.codeit.monew.domain.article.controller;

import com.codeit.monew.domain.article.controller.docs.ArticleControllerDocs;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.service.ArticleService;
import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;
import com.codeit.monew.domain.articleView.service.ArticleViewService;
import com.codeit.monew.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController implements ArticleControllerDocs {
    private final ArticleService articleService;
    private final ArticleViewService articleViewService;

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> articleDetails(@PathVariable UUID articleId,
                                                     @RequestHeader("Monew-Request-User-ID") UUID userId) {
        return ResponseEntity.ok(articleService.searchByUserIdAndArticleId(userId, articleId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ArticleDto>> articleList
            (@ParameterObject ArticleSearchRequest request,
             @RequestHeader("Monew-Request-User-ID") UUID userId) {
        return ResponseEntity.ok(articleService.searchByKeyword(request, userId));
    }

    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> articleViewAdd(@PathVariable UUID articleId,
                                                         @RequestHeader("Monew-Request-User-ID") UUID userId) {
        return ResponseEntity.ok(articleViewService.createArticleView(articleId, userId));
    }

    @GetMapping("/sources")
    public ResponseEntity<List<ArticleSource>> sourceList() {
        List<ArticleSource> sources = List.of(ArticleSource.values());
        return ResponseEntity.ok(sources);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable("articleId") UUID articleId) {
        articleService.softDelete(articleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{articleId}/hard")
    public ResponseEntity<Void> deleteArticleHard(@PathVariable("articleId") UUID articleId) {
        articleService.hardDelete(articleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
