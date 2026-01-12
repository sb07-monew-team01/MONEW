package com.codeit.monew.domain.article.infrastructure.naver.client;

import com.codeit.monew.domain.article.infrastructure.naver.dto.NaverApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NaverArticleClient {

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private final RestClient restClient;

    public NaverArticleClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://openapi.naver.com")
                .build();
    }

    public NaverApiResponse searchArticle(String query, int display, int start) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/news.json")
                        .queryParam("query", query)
                        .queryParam("display", display)
                        .queryParam("start", start)
                        .build())
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .retrieve()
                .body(NaverApiResponse.class); // 바로 body 반환
    }
}

