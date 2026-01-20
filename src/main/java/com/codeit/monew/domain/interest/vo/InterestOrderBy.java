package com.codeit.monew.domain.interest.vo;

import java.util.Optional;

public enum InterestOrderBy {
    NAME,
    SUBSCRIBER_COUNT;

    public static Optional<InterestOrderBy> fromString(String value) {
        return Optional.ofNullable(value == null ? null : InterestOrderBy.valueOf(value.toUpperCase()));
    }
}
