package com.codeit.monew.domain.interest.service;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.*;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.global.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService{
    private final InterestRepository interestRepository;

    private static final double SIMILARITY_THRESHOLD = 0.8;
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    @Override
    public Interest create(String name, List<String> keywords) {
        if(keywords == null){
            throw new KeywordValidException(ErrorCode.INTEREST_NULL_KEYWORD);
        }
        if(keywords.isEmpty()){
            throw new KeywordValidException(ErrorCode.INTEREST_EMPTY_KEYWORD);
        }
        checkSimilarity(name);
        checkDuplicateKeyword(keywords);
        return new Interest(name, keywords);
    }

    //80% 이상 유사한가를 체크하고, 유사하면 예외를 발생시키는 메소드
    private void checkSimilarity(String name) {
        interestRepository.findAll().forEach(interest -> {
            double score = similarity.apply(interest.getName(), name);
            if(score >= SIMILARITY_THRESHOLD){
                throw new InterestNameTooSimilarException(
                    ErrorCode.INTEREST_NAME_TOO_SIMILAR,
                    Map.of("입력값", name, "비교값", interest.getName(), "유사도", score)
                );
            }
        });
    }

    //같은 관심사 내에 중복 키워드가 있는지 체크하고, 있으면 예외를 발생시키는 메소드
    private void checkDuplicateKeyword(List<String> keywords) {
        if(keywords.size() != keywords.stream().distinct().count()){
            throw new KeywordValidException(
                    ErrorCode.INTEREST_KEYWORD_DUPLICATE
            );
        }
    }
}


