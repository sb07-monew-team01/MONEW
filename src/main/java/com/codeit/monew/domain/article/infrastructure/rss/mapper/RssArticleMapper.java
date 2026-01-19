package com.codeit.monew.domain.article.infrastructure.rss.mapper;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.interest.entity.Interest;
import com.rometools.rome.feed.synd.SyndEntry;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class RssArticleMapper {

    public ArticleCreateRequest toArticleCreateRequest(SyndEntry entry, ArticleSource source) {

        String title = entry.getTitle();
        String link = entry.getLink();
        Date published = entry.getPublishedDate();

        if (title == null || title.isBlank()) return null;
        if (link == null || link.isBlank()) return null;
        if (published == null) return null;

        String summary = "";
        if (entry.getDescription() != null && entry.getDescription().getValue() != null && !entry.getDescription().getValue().isBlank()) {
            summary = Jsoup.parse(entry.getDescription().getValue()).text();
        } else {
            summary = title;
        }

        return new ArticleCreateRequest (
                source,
                link,
                title,
                parsePublicDate(entry.getPublishedDate()),
                summary
        );
    }

    private LocalDateTime parsePublicDate(Date published) {
        return published.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public ArticleCreateRequest addTags(ArticleCreateRequest article, Interest matched) {
        if (matched == null) return article;

        List<String> tags = new ArrayList<>();
        tags.add("#" + matched.getName());
        matched.getKeywords().forEach(k -> tags.add("#" + k.getKeyword()));

        String newSummary = article.summary() + "<br>" + String.join(" ", tags);

        // ArticleCreateRequest가 불변 객체이기 때문에, 새로 생성해야함.
        return new ArticleCreateRequest(
                article.source(),
                article.sourceUrl(),
                article.title(),
                article.publishDate(),
                newSummary
        );
    }
}