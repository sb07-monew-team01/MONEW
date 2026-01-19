package com.codeit.monew.domain.interest.service;

import com.codeit.monew.domain.interest.dto.request.InterestCreatedRequest;
import com.codeit.monew.domain.interest.dto.request.InterestCursorPageRequest;
import com.codeit.monew.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.monew.domain.interest.dto.response.InterestCommonResponse;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.web.InterestNotFoundException;
import com.codeit.monew.domain.interest.mapper.InterestMapper;
import com.codeit.monew.domain.interest.mapper.InterestQueryMapper;
import com.codeit.monew.domain.interest.policy.InterestNamePolicy;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.interest.repository.InterestRepositoryCustomImpl;
import com.codeit.monew.domain.interestuser.repository.InterestUserRepository;
import com.codeit.monew.global.dto.PageResponse;
import com.codeit.monew.global.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService{
    private final InterestRepository interestRepository;
    private final InterestNamePolicy interestNamePolicy;
    private final InterestRepositoryCustomImpl interestRepositoryCustom;
    private final InterestQueryMapper interestQueryMapper;
    private final InterestMapper interestMapper;
    private final InterestUserRepository interestUserRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterestCommonResponse> getInterests(UUID userId, InterestCursorPageRequest request) {
        Slice<Interest> slice = interestRepositoryCustom.findAllByCursor(
                interestQueryMapper.toQuery(request)
        );
        List<InterestCommonResponse> content = slice.getContent().stream()
            .map(interest ->
                interestMapper.toDto(interest,
                interestUserRepository.existsByUserIdAndInterestId(userId, interest.getId()))
            ).toList();

        String nextCursor = null;
        LocalDateTime nextAfter = null;

        if(slice.hasNext() && !slice.getContent().isEmpty()){
            Interest last = slice.getContent().get(slice.getContent().size() - 1);
            nextAfter = last.getCreatedAt();
            nextCursor = switch (request.orderBy()) {
                case NAME -> last.getName();
                case SUBSCRIBER_COUNT -> String.valueOf(last.getSubscriberCount());
            };
        }

        return new PageResponse<>(content, nextCursor, nextAfter, slice.getSize(),0L, slice.hasNext());
    }

    @Override
    @Transactional
    public InterestCommonResponse create(InterestCreatedRequest request){
        interestNamePolicy.apply(request.name(), interestRepository.findAll());
        return interestMapper.toDto(
                interestRepository.save(new Interest(request.name(), request.keywords())),
                false
        );
    }

    @Override
    @Transactional
    public InterestCommonResponse editKeywords(UUID userId, InterestUpdateRequest request){
        Interest interest = findById(request.interestId());

        return interestMapper.toDto(
                interest.update(request.keywords()),
                interestUserRepository.existsByUserIdAndInterestId(userId, interest.getId())
        );
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