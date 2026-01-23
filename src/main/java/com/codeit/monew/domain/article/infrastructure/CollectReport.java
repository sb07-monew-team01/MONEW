package com.codeit.monew.domain.article.infrastructure;

public record CollectReport(
        // 총 수집한 기사 개수 (중복 필터 처리 및 관심사 필터(RSS) 처리 전)
        int fetchedArticle,
        int savedArticle // 저장된 기사 개수
) {
}
