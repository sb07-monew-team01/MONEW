package com.codeit.monew.domain.article.exception;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.global.enums.ErrorCode;
import com.codeit.monew.global.exception.MonewException;

import java.util.Map;

public class ArticleException extends MonewException {
    public ArticleException(ErrorCode errorCode, Map<String, Object> details) {
        super(Article.class, errorCode, details);
    }
}
