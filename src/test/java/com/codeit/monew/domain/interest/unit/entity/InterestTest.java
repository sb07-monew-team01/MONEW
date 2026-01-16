package com.codeit.monew.domain.interest.unit.entity;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interestkeyword.entity.InterestKeyword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InterestTest {

    @Nested
    @DisplayName("관심사 생성")
    class CreateInterestTest {
        @Test
        @DisplayName("성공: 유효한 이름과 키워드가 주어지면 관심사가 생성된다")
        void success_create_interest(){
            // given
            String name = "백엔드";
            List<String> keywords = List.of("java", "spring");

            // when
            Interest interest = new Interest(name, keywords);

            // then
            assertThat(interest).isNotNull();
            assertThat(interest.getName()).isEqualTo(name);
            assertThat(interest.getKeywords())
                    .extracting(InterestKeyword::getKeyword)
                    .containsExactlyElementsOf(keywords);
        }
    }
}
