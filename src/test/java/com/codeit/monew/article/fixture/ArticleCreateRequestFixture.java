package com.codeit.monew.article.fixture;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.ArticleSource;

import java.time.LocalDateTime;

public class ArticleCreateRequestFixture {
    public static ArticleCreateRequest createDefault() {
        return  new ArticleCreateRequest(
                ArticleSource.NAVER,
                "http://target.com",
                "test-title",
                LocalDateTime.now(),
                "test summary"
        );
    }
}
