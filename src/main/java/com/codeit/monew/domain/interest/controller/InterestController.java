package com.codeit.monew.domain.interest.controller;

import com.codeit.monew.domain.interest.dto.request.InterestCursorPageRequest;
import com.codeit.monew.domain.interest.dto.response.InterestCommonResponse;
import com.codeit.monew.domain.interest.service.InterestService;
import com.codeit.monew.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
