package com.codeit.monew.domain.interest.vo;

public enum SortDirection {
    ASC, DESC;

    public boolean isAsc() {
        return this == ASC;
    }
}
