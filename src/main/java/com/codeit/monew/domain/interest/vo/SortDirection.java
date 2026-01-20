package com.codeit.monew.domain.interest.vo;

import java.util.Optional;

public enum SortDirection {
    ASC, DESC;

    public boolean isAsc() {
        return this == ASC;
    }

    public static Optional<SortDirection> fromString(String value){
        return Optional.ofNullable(value == null ? null : SortDirection.valueOf(value.toUpperCase()));
    }
}
