package com.codeit.monew.domain.notification.entity;

public enum ResourceType {

    COMMENT("%s님이 나의 댓글을 좋아합니다."),

    INTEREST("%s%s 관련된 기사가 %d건 등록되었습니다.");

    private final String content;

    ResourceType(String content) {
        this.content = content;
    }

    public String format(String name, int count) {
        return String.format(
                content,
                name,
                attachGwaWa(name),
                count
        );
    }

    public String format(String name) {
        return String.format(content, name);
    }

    //한글 받침(과,와) 영어는  와
    private static String attachGwaWa(String word) {
        if (word == null || word.isBlank()) {
            return "와";
        }
        //마지막글자 추출
        char lastChar = word.charAt(word.length() - 1);

        //글자크기판별은 유니코드값으로 한다  44032(가) ~ 55203(힣) 범위다
        //벗어나면 한국어가 아니니 영처리
        if (lastChar < '가' || lastChar > '힣') {
            return "와";
        }
       //즉 한글이면
        //종성의 로테이션은 0~27 즉 28개로 돌아간다
        // 받침의 로테이션은 0(없다) ~ ㅎ(27) 28가지로테이션이다
        //글자는 계산은 유니코드로 하니
        //처음시작  받침이 없는건 0번째 가 거 나 하 히 허 해...등 0번째 부터다
        //즉 글자 - 44032(한글 유니코드의 시작)
        //11172(0 ~ 11171) 가지의 로테이션에 28번주기로 받침이 없는번호가 나온다
        return ((lastChar - '가') % 28 == 0) ? "와" : "과";
    }

}
