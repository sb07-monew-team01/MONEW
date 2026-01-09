package com.codeit.monew.domain.interest.policy;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.InterestNameTooSimilarException;
import com.codeit.monew.global.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SimilarityInterestNamePolicy implements InterestNamePolicy {
    private static final double SIMILARITY_THRESHOLD = 0.8;
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    //80% 이상 유사한가를 체크하고, 유사하면 예외를 발생시키는 메소드
    @Override
    public void apply(String name, List<Interest> list) {
        list.forEach(interest -> {
            double score = similarity.apply(interest.getName(), name);
            if(score >= SIMILARITY_THRESHOLD){
                throw new InterestNameTooSimilarException(
                        ErrorCode.INTEREST_NAME_TOO_SIMILAR,
                        Map.of("입력값", name, "비교값", interest.getName(), "유사도", score)
                );
            }
        });
    }
}