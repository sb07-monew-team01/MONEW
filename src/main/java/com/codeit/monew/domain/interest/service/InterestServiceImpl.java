package com.codeit.monew.domain.interest.service;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.InterestNotFoundException;
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
        return interestRepository.save(new Interest(name, keywords));
    }

    @Override
    public Interest editKeywords(UUID id, List<String> keywords) {
        Interest interest = findById(id);
        return interest.update(keywords);
    }

    public void delete(UUID id) {
        findById(id);
        interestRepository.deleteById(id);
    }

    public Interest findById(UUID id) {
        return interestRepository.findById(id).orElseThrow(
                () -> new InterestNotFoundException(ErrorCode.INTEREST_NOT_FOUND));
    }
}


