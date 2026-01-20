package com.codeit.monew.domain.comment.dto.request;

public enum CommentOrderBy {
    CREATED_AT("createdAt"),
    LIKE_COUNT("likeCount");

    private final String field;

    CommentOrderBy(String field) {
        this.field = field;
    }

    public String field() {
        return field;
    }
}
