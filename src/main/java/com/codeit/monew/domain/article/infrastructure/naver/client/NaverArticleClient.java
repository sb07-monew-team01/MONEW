package com.codeit.monew.domain.article.infrastructure.naver.client;

import com.codeit.monew.domain.article.infrastructure.exception.naver.NaverApiAuthenticationException;
import com.codeit.monew.domain.article.infrastructure.exception.naver.NaverApiRateLimitException;
import com.codeit.monew.domain.article.infrastructure.exception.naver.NaverApiServerException;
import com.codeit.monew.domain.article.infrastructure.naver.dto.NaverApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NaverArticleClient {

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://openapi.naver.com")
            .build();


    public NaverApiResponse searchArticle(String query, int display, int start) {
        try {
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
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        int statusCode = response.getStatusCode().value();

                        if (statusCode == 401 || statusCode == 403) {
                            throw new NaverApiAuthenticationException(
                                    String.format("네이버 API 인증 Failed: %d", statusCode)
                            );
                        }

                        if (statusCode == 429) {
                            throw new NaverApiRateLimitException(
                                    String.format("네이버 API 호출 한도 초과: %d", statusCode)
                            );
                        }
                        throw new NaverApiServerException(
                                String.format("네이버 API Client 오류: %d", statusCode)
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new NaverApiServerException(
                                String.format("네이버 API Server 오류 : %d", response.getStatusCode().value())
                        );
                    })
                    .body(NaverApiResponse.class); // 바로 body 반환
        } catch (NaverApiAuthenticationException | NaverApiRateLimitException | NaverApiServerException e) {
            throw e;
        } catch (Exception e) { // 호출 실패 시 발생 에러
            throw new NaverApiServerException("API 호출 실패");
        }
    }
}

