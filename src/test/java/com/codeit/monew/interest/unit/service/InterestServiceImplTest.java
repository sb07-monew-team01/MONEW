package com.codeit.monew.interest.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class InterestServiceImplTest {

    InterestService interestService;

    @BeforeEach
    void setUp() {
        interestService = new InterestServiceImpl();
    }

    @Nested
    @DisplayName("관심사 생성")
    class CreateInterest {

        @Test
        @DisplayName("성공: 유효한 이름과 키워드가 주어지면 관심사가 생성된다")
        void success_create_interest() {
            //given
            String name = "백엔드";
            List<String> keywords = Arrays.asList("java", "spring");

            // when
            Interest result = interestService.create(name, keywords);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(name);
            assertThat(result.getKeywords()).containsExactlyElementsOf(keywords);
        }
    }
}