package com.codeit.monew.domain.interest.controller;

import com.codeit.monew.domain.interest.dto.request.InterestCreatedRequest;
import com.codeit.monew.domain.interest.dto.request.InterestCursorPageRequest;
import com.codeit.monew.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.monew.domain.interest.dto.response.InterestCommonResponse;
import com.codeit.monew.domain.interest.service.InterestService;
import com.codeit.monew.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interests")
public class InterestController {
    private final InterestService interestService;

    @GetMapping
    public ResponseEntity<PageResponse<InterestCommonResponse>> InterestList(
        @RequestHeader("Monew-Request-User-ID") UUID userId,
        InterestCursorPageRequest request
    ){
        return ResponseEntity.ok(interestService.getInterests(userId, request));
    }

    @PostMapping
    public ResponseEntity<InterestCommonResponse> InterestCreate(
            @RequestBody InterestCreatedRequest request
    ){
        InterestCommonResponse saved = interestService.create(request);
        return ResponseEntity.created(URI.create(saved.interestId().toString())).body(saved);
    }

    @PatchMapping
    public ResponseEntity<InterestCommonResponse> InterestEdit(
            @RequestHeader("Monew-Request-User-ID") UUID userId,
            @RequestBody InterestUpdateRequest request
    ){
        InterestCommonResponse updated = interestService.editKeywords(userId, request);
        return ResponseEntity.ok(updated);
    }
}
