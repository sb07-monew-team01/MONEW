package com.codeit.monew.domain.notification.entity;

public enum ResourceType {

    COMMENT("%s님이 나의 댓글을 좋아합니다."),

    INTEREST("%s와 관련된 기사가 %d건 등록되었습니다.");

    private final String content;

    ResourceType(String content) {
        this.content = content;
    }
    public String format(String name, int count) {
        return String.format(content, name, count);
    }

    public String format(String name) {
        return String.format(content, name);
    }

}
