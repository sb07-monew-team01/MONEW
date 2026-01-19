package com.codeit.monew.domain.interest.service;

import com.codeit.monew.domain.interest.dto.InterestCursorQuery;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.web.InterestNotFoundException;
import com.codeit.monew.domain.interest.policy.InterestNamePolicy;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.interest.repository.InterestRepositoryCustomImpl;
import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.SortDirection;
import com.codeit.monew.global.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService{
    private final InterestRepository interestRepository;
    private final InterestNamePolicy interestNamePolicy;
    private final InterestRepositoryCustomImpl interestRepositoryCustom;

    @Override
    @Transactional(readOnly = true)
    public Slice<Interest> getInterests(
            String keyword,
            String orderBy,
            String direction,
            String cursor,
            String after,
            Integer limit
    ){
        InterestOrderBy interestOrderBy = InterestOrderBy.valueOf(orderBy.toUpperCase());
        SortDirection sortDirection = SortDirection.valueOf(direction);
        String keywordValue = StringUtils.hasText(keyword) ? keyword.trim() : null;

        String nameCursor = null;
        Long subscriberCountCursor = null;

        switch (interestOrderBy){
            case NAME:
                nameCursor = cursor;
                break;
            case SUBSCRIBER_COUNT:
                subscriberCountCursor = Long.parseLong(cursor);
                break;
        }

        InterestCursorQuery query = new InterestCursorQuery(
            interestOrderBy,
            sortDirection,
            nameCursor,
            subscriberCountCursor,
            after,
            limit,
            keywordValue
        );

        return interestRepositoryCustom.findAllByCursor(query);
    }

    @Override
    @Transactional
    public Interest create(String name, List<String> keywords){
        interestNamePolicy.apply(name, interestRepository.findAll());
        return interestRepository.save(new Interest(name, keywords));
    }

    @Override
    @Transactional
    public Interest editKeywords(UUID id, List<String> keywords){
        Interest interest = findById(id);
        return interest.update(keywords);
    }

    @Override
    @Transactional
    public void delete(UUID id){
        findById(id);
        interestRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Interest findById(UUID id){
        return interestRepository.findById(id).orElseThrow(
                () -> new InterestNotFoundException(ErrorCode.INTEREST_NOT_FOUND));
    }
}