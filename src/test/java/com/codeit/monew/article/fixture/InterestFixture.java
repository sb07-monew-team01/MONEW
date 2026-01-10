package com.codeit.monew.article.fixture;

import com.codeit.monew.domain.interest.entity.Interest;

import java.util.List;

public class InterestFixture {
    // 그냥 DB에 들어있는 전체 관심사 느낌
    public static List<Interest> createDefault() {
        return List.of(
                new Interest("경제", List.of("금리", "환율")),
                new Interest("IT", List.of("AI", "클라우드"))
        );
    }
}
