package com.codeit.monew.domain.article.infrastructure;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.infrastructure.exception.rss.RssException;
import com.codeit.monew.domain.article.infrastructure.exception.rss.RssFetchException;
import com.codeit.monew.domain.article.infrastructure.exception.rss.RssParseException;
import com.codeit.monew.domain.article.infrastructure.rss.client.RssArticleClient;
import com.codeit.monew.domain.article.infrastructure.rss.mapper.RssArticleMapper;
import com.codeit.monew.domain.article.matcher.ArticleMatcher;
import com.codeit.monew.domain.interest.entity.Interest;
import com.rometools.rome.feed.synd.SyndFeed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class RssArticleCollector implements ArticleCollector {

    private final RssArticleClient rssArticleClient;
    private final RssArticleMapper rssArticleMapper;
    private final ArticleMatcher articleMatcher;

    private static final Map<String, ArticleSource> RSS_SOURCES = Map.of(
            "https://www.hankyung.com/feed/all-news", ArticleSource.HANKYUNG,
            "https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml", ArticleSource.CHOSUN,
            "https://www.yonhapnewstv.co.kr/browse/feed/", ArticleSource.YEONHAP
    );

    @Override
    public List<ArticleCreateRequest> collect(List<Interest> interests) {

        List<ArticleCreateRequest> results = new ArrayList<>();

        for (Map.Entry<String, ArticleSource> entry : RSS_SOURCES.entrySet()) {
            String url = entry.getKey();
            ArticleSource source = entry.getValue();
            try {
                SyndFeed feed = rssArticleClient.fetch(url);

                results.addAll(
                        feed.getEntries().stream()
                                .map(syndEntry -> rssArticleMapper.toArticleCreateRequest(syndEntry, source))
                                .filter(Objects::nonNull)
                                .filter(req -> articleMatcher.match(req, interests))
                                .map(req -> {
                                    Interest interest = interests.stream()
                                            .filter(i -> articleMatcher.match(req, List.of(i)))
                                            .findFirst()
                                            .orElse(null);
                                    return rssArticleMapper.addTags(req, interest);
                                })
                                .toList()
                );
            } catch (RssFetchException e) {
                log.warn("RSS 수집 실패: url={}, error={}", url, e.getMessage());
            } catch (RssParseException e) {
                log.warn("RSS 파싱 오류: url = {}, error={}", url, e.getMessage());
            } catch (RssException e) {
                log.error("RSS 처리 중 예상치 못한 오류 url = {}, error={}", url, e.getMessage());
            }
        }

        return results;
    }

}
