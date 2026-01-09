package com.codeit.monew.article.fixture;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.ArticleSource;

import java.time.LocalDateTime;
import java.util.Random;

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

    public static ArticleCreateRequest createDummy(int s, int d) {

        ArticleSource source = switch (s) {
            case 0 -> ArticleSource.NAVER;
            case 1 -> ArticleSource.HANKYUNG;
            case 2 -> ArticleSource.CHOSUN;
            default -> ArticleSource.YEONHAP;
        };

        Random random = new Random();

        return  new ArticleCreateRequest(
                source,
                "http://" + random.nextInt(100) + source +".com",
                source.name() + " 뉴스_" + random.nextInt(100),
                LocalDateTime.now().plusDays(d),
                "test summary"
        );
    }
}
