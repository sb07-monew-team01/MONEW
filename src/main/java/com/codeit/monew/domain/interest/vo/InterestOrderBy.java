package com.codeit.monew.domain.interest.vo;

import java.util.Optional;

public enum InterestOrderBy {
    NAME,
    SUBSCRIBERCOUNT;

    public static Optional<InterestOrderBy> fromString(String value) {
        return Optional.ofNullable(value == null ? null : InterestOrderBy.valueOf(value.toUpperCase()));
    }
}
