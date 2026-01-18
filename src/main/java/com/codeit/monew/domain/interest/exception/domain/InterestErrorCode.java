package com.codeit.monew.domain.interest.exception.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InterestErrorCode {
    KEYWORD_DUPLICATE("같은 관심사 내에 중복 키워드가 존재합니다."),
    EMPTY_KEYWORD("관심사에 등록된 키워드가 없습니다."),
    NULL_KEYWORD("키워드가 null일 수 없습니다."),
    TOO_MANY_KEYWORD("키워드가 10개를 초과합니다.");

    private final String description;
}
