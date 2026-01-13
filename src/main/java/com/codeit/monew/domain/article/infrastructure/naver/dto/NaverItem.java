package com.codeit.monew.domain.article.infrastructure.naver.dto;

public record NaverItem(
   String title,
   String originallink,
   String link,
   String description,
   String pubDate
) {
}
