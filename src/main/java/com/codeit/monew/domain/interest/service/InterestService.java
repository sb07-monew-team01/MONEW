package com.codeit.monew.domain.interest.service;

import com.codeit.monew.domain.interest.dto.request.InterestCreatedRequest;
import com.codeit.monew.domain.interest.dto.request.InterestCursorPageRequest;
import com.codeit.monew.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.monew.domain.interest.dto.response.InterestCommonResponse;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.global.dto.PageResponse;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

public interface InterestService {
    InterestCommonResponse create(InterestCreatedRequest request);
    InterestCommonResponse editKeywords(UUID userId, InterestUpdateRequest request);
    void delete(UUID id);
    Interest findById(UUID id);
    PageResponse<InterestCommonResponse> getInterests(UUID userId, InterestCursorPageRequest request);
}
