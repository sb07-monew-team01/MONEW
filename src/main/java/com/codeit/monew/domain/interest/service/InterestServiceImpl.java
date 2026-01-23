package com.codeit.monew.domain.interest.service;

import com.codeit.monew.domain.interest.dto.query.InterestCursorQuery;
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
import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.NextCursor;
import com.codeit.monew.domain.interestuser.repository.InterestUserRepository;
import com.codeit.monew.global.aop.annotation.LogExecution;
import com.codeit.monew.global.aop.annotation.LogTag;
import com.codeit.monew.global.dto.PageResponse;
import com.codeit.monew.global.enums.ErrorCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@LogExecution(tag= LogTag.INTEREST)
public class InterestServiceImpl implements InterestService{
    private final InterestRepository interestRepository;
    private final InterestNamePolicy interestNamePolicy;
    private final InterestRepositoryCustomImpl interestRepositoryCustom;
    private final InterestQueryMapper interestQueryMapper;
    private final InterestMapper interestMapper;
    private final InterestUserRepository interestUserRepository;
    private final EntityManager em;


    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterestCommonResponse> getInterests(UUID userId, InterestCursorPageRequest request) {
        InterestCursorQuery query = interestQueryMapper.toQuery(request);
        Slice<Interest> slice = interestRepositoryCustom.findAllByCursor(query);

        List<InterestCommonResponse> content = slice.getContent().stream()
        .map(interest ->
            interestMapper.toDto(interest,
                interestUserRepository.existsByUserIdAndInterestId(userId, interest.getId())
            )
        ).toList();
        NextCursor nextCursor = NextCursor.from(slice,
                InterestOrderBy.fromString(request.orderBy()).orElse(InterestOrderBy.NAME));

        return new PageResponse<>(
                content,
                nextCursor.getCursor(),
                nextCursor.getAfter(),
                slice.getSize(),
                0L,
                slice.hasNext()
        );
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
    public InterestCommonResponse editKeywords(UUID interestId, InterestUpdateRequest request){
        Interest interest = findById(interestId);
        interest.clearKeywords();

        em.flush();

        return interestMapper.toDto(
                interest.update(request.keywords()),
                null
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