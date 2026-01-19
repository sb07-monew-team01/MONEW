package com.codeit.monew.domain.article.controller;

import com.codeit.monew.domain.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleDeleteController {
    private final ArticleService articleService;

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
