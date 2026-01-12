package com.codeit.monew.domain.interest.service;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.KeywordValidException;
import com.codeit.monew.domain.interest.policy.InterestNamePolicy;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.global.enums.ErrorCode;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService{
    private final InterestRepository interestRepository;
    private final InterestNamePolicy interestNamePolicy;

    @Override
    public Interest create(String name, List<String> keywords) {
        interestNamePolicy.apply(name, interestRepository.findAll());
        checkKeyword(keywords);

        return interestRepository.save(new Interest(name, keywords));
    }

    //키워드 검증 메소드 분리
    private void checkKeyword(List<String> keywords) {
        if(keywords == null){
            throw new KeywordValidException(ErrorCode.INTEREST_NULL_KEYWORD);
        }
        if(keywords.isEmpty()){
            throw new KeywordValidException(ErrorCode.INTEREST_EMPTY_KEYWORD);
        }
        if(keywords.size() > 10){
            throw new KeywordValidException(ErrorCode.TOO_MANY_KEYWORD);
        }
        checkDuplicateKeyword(keywords);
    }

    //같은 관심사 내에 중복 키워드가 있는지 체크하고, 있으면 예외를 발생시키는 메소드
    private void checkDuplicateKeyword(List<String> keywords) {
        if(keywords.size() != keywords.stream().distinct().count()){
            throw new KeywordValidException(
                    ErrorCode.INTEREST_KEYWORD_DUPLICATE
            );
        }
    }

    @Override
    public Interest editKeywords(UUID id, List<String> keywords) {
        Interest interest = interestRepository.findById(id).get();
        return interest.update(keywords);
    }
}


