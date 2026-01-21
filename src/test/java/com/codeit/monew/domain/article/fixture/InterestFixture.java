package com.codeit.monew.domain.article.fixture;

import com.codeit.monew.domain.interest.entity.Interest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InterestFixture {
    // 그냥 DB에 들어있는 전체 관심사 느낌
    public static List<Interest> createDefault() {
        return List.of(
                new Interest("경제", List.of("금리", "환율")),
                new Interest("IT", List.of("AI", "클라우드"))
        );
    }

    public static Interest create(String name, List<String> keywords) {
        Interest interest = new Interest(name, keywords);
        ReflectionTestUtils.setField(interest, "id", UUID.randomUUID());
        return interest;
    }

    public static List<Interest> createMultiple(Interest... interests) {
        return Arrays.asList(interests);
    }

}
