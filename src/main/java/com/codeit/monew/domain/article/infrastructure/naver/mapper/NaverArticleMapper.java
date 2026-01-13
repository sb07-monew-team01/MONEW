package com.codeit.monew.domain.article.infrastructure.naver.mapper;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.infrastructure.naver.dto.NaverItem;
import com.codeit.monew.domain.interest.entity.Interest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class NaverArticleMapper {

    public ArticleCreateRequest toArticleCreateRequest(NaverItem item, Interest interest
    ) {
        return new ArticleCreateRequest(
                ArticleSource.NAVER,
                item.originallink(),
                item.title(),
                parsePublicDate(item.pubDate()),
                addTagDescription(item.description(), interest)
        );
    }

    private LocalDateTime parsePublicDate(String pubDate) {
        return ZonedDateTime
                .parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME)
                .toLocalDateTime();
    }

    private String addTagDescription(String description, Interest interest) {
        List<String> tags = new ArrayList<>();

        tags.add("#" + interest.getName());
        interest.getKeywords().forEach(keyword -> tags.add("#"+ keyword));

        return description + "<br>" + String.join(" ", tags);
    }
}
