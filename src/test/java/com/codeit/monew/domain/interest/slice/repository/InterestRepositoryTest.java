package com.codeit.monew.domain.interest.slice.repository;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.interestkeyword.entity.InterestKeyword;
import com.codeit.monew.global.config.TestJpaAuditing;
import com.codeit.monew.global.config.TestQueryDslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestQueryDslConfig.class, TestJpaAuditing.class})
@ActiveProfiles("test")
public class InterestRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private InterestRepository interestRepository;

    @Nested
    @DisplayName("관심사 저장")
    class SaveInterestTest {
        @Test
        @DisplayName("성공: Interest 저장 시 InterestKeyword가 함께 저장된다")
        void save_interest_with_interestKeyword(){
            // given
            Interest interest = new Interest("백엔드", List.of("Java", "Spring"));

            // when
            Interest saved = interestRepository.save(interest);
            em.flush();
            em.clear();

            // then
            Interest result = interestRepository.findById(saved.getId()).orElseThrow();
            assertThat(result.getKeywords())
                    .extracting(InterestKeyword::getKeyword)
                    .containsExactly("Java", "Spring");
        }
    }

    @Nested
    @DisplayName("관심사 수정")
    class UpdateInterestTest {
        @Test
        @DisplayName("성공: 관심사의 ArrayList에서 키워드를 제거하면 DB에서도 삭제된다")
        void update_interest_remove_keyword(){
            // given
            Interest saved = interestRepository.save(
                    new Interest("백엔드", List.of("Java", "Spring"))
            );
            em.flush();
            em.clear();
            Interest interest = interestRepository.findById(saved.getId()).orElseThrow();

            // when
            interest.update(List.of("java"));
            em.flush();
            em.clear();

            // then
            Interest updated = interestRepository.findById(interest.getId()).orElseThrow();
            assertThat(updated.getKeywords())
                    .extracting(InterestKeyword::getKeyword)
                    .containsExactly("java");

        }
    }
}
