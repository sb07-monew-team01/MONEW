package com.codeit.monew.domain.interest.util;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

public class InterestTestUtils {
    private static final String[] WORDS = {
            "코딩", "스터디", "프로젝트", "개발", "테스트", "자바", "리액트", "백엔드", "프론트엔드", "API",
            "Code", "Stack", "Flow", "Build", "Test", "Spring", "React", "Java", "Backend", "Frontend",
            "요리", "동물", "병원", "정치", "대통령", "게임", "축구", "환율", "주식", "성공"
    };

    public static void createInterests(InterestRepository interestRepository, TestEntityManager entityManager, int count) {
        interestRepository.deleteAll();
        if (count == 0) return;

        for (int i = 1; i <= count; i++) {
            String name = randomWord() + " " + randomWord() + i;
            String keyword = randomWord() + " " + randomWord() + i;
            Interest interest = new Interest(name, List.of(keyword));

            // 구독자 수 랜덤화 (0~10)
            Long subscriberCount = (long) (Math.random() * 10);
            // createdAt 랜덤화 (최근 365일)
            LocalDateTime createdAt = LocalDateTime.now()
                    .minusDays((long) (Math.random() * 365))
                    .minusHours((long) (Math.random() * 24))
                    .minusMinutes((long) (Math.random() * 60));

            ReflectionTestUtils.setField(interest, "subscriberCount", subscriberCount);
            ReflectionTestUtils.setField(interest, "createdAt", createdAt);

            interestRepository.save(interest);

            entityManager.flush();
            entityManager.clear();
        }
    }

    private static String randomWord() {
        return WORDS[(int) (Math.random() * WORDS.length)];
    }
}
