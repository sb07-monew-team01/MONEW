package com.codeit.monew.domain.article.infrastructure.rss.client;

import com.codeit.monew.domain.article.infrastructure.exception.rss.RssException;
import com.codeit.monew.domain.article.infrastructure.exception.rss.RssFetchException;
import com.codeit.monew.domain.article.infrastructure.exception.rss.RssParseException;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RssArticleClient {

    public SyndFeed fetch(String rssUrl) {
        try {
            URLConnection connection = new URL(rssUrl).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (InputStream is = connection.getInputStream()) {
                return new SyndFeedInput()
                        .build(new InputStreamReader(is, StandardCharsets.UTF_8));
            }

        } catch (SocketTimeoutException e) {
            // 타임아웃
            throw new RssFetchException(
                    String.format("RSS 연결 타임아웃 url = %s", rssUrl),
                    e
            );

        } catch (IOException e) {
            // 네트워크 오류 (연결 실패, 404, 500 등)
            throw new RssFetchException(
                    String.format("RSS 가져오기 실패 url=%s, error=%s", rssUrl, e.getMessage()),
                    e
            );

        } catch (FeedException e) {
            // XML 파싱 오류 (잘못된 RSS 형식)
            throw new RssParseException(
                    String.format("RSS 파싱 실패 url=%s, error=%s", rssUrl, e.getMessage()),
                    e
            );

        } catch (Exception e) {
            // 기타 예상치 못한 오류
            throw new RssException(
                    String.format("RSS 처리 중 예상치 못한 오류 url=%s, error=%s", rssUrl, e.getMessage()),
                    e
            );
        }
    }
}