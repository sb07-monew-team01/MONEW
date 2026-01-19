package com.codeit.monew.domain.article.controller;

import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleSearchController {

    private final ArticleService articleService;

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> getArticle(@PathVariable  UUID articleId,
                                                 @RequestHeader("Monew-Request-User-ID") UUID userId) {
        return ResponseEntity.ok(articleService.searchByUserIdAndArticleId(userId, articleId));
    }
}
