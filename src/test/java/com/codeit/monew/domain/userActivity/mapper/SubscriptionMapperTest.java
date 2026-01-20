package com.codeit.monew.domain.userActivity.mapper;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.userActivity.dto.SubscriptionDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구독 Mapper")
class SubscriptionMapperTest {

    SubscriptionMapper subscriptionMapper = new SubscriptionMapper();

    @Nested
    @DisplayName("DTO 변환")
    class ToDto {

        @Test
        @DisplayName("InterestUser를 SubscriptionDto로 변환할 수 있다")
        void toDto() {
            // given
            User user = Instancio.create(User.class);
            Interest interest = new Interest("IT", List.of("Java", "Spring", "Kotlin"));
            InterestUser interestUser = new InterestUser(user, interest);

            // when
            SubscriptionDto dto = subscriptionMapper.toDto(interestUser);

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.interestId()).isEqualTo(interest.getId());
            assertThat(dto.interestName()).isEqualTo("IT");
            assertThat(dto.interestKeywords()).containsExactly("Java", "Spring", "Kotlin");
        }

        @Test
        @DisplayName("키워드 목록이 올바르게 변환된다")
        void toDto_keywords() {
            // given
            User user = Instancio.create(User.class);
            Interest interest = new Interest("Backend", List.of("Spring Boot", "JPA", "QueryDSL"));
            InterestUser interestUser = new InterestUser(user, interest);

            // when
            SubscriptionDto dto = subscriptionMapper.toDto(interestUser);

            // then
            assertThat(dto.interestKeywords()).hasSize(3);
            assertThat(dto.interestKeywords()).containsExactlyInAnyOrder("Spring Boot", "JPA", "QueryDSL");
        }
    }
}
