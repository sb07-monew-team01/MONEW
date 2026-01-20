package com.codeit.monew.domain.interest.controller;

import com.codeit.monew.domain.interest.dto.request.InterestCreatedRequest;
import com.codeit.monew.domain.interest.dto.request.InterestCursorPageRequest;
import com.codeit.monew.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.monew.domain.interest.dto.response.InterestCommonResponse;
import com.codeit.monew.domain.interest.dto.response.InterestSubScriptionResponse;
import com.codeit.monew.domain.interest.service.InterestService;
import com.codeit.monew.domain.interestuser.service.InterestUserService;
import com.codeit.monew.global.dto.PageResponse;
import jakarta.validation.Valid;
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
    private final InterestUserService interestUserService;

    @GetMapping
    public ResponseEntity<PageResponse<InterestCommonResponse>> InterestList(
        @RequestHeader("Monew-Request-User-ID") UUID userId,
        InterestCursorPageRequest request
    ){
        return ResponseEntity.ok(interestService.getInterests(userId, request));
    }

    @PostMapping
    public ResponseEntity<InterestCommonResponse> InterestCreate(
            @RequestBody @Valid InterestCreatedRequest request
    ){
        InterestCommonResponse saved = interestService.create(request);
        return ResponseEntity.created(URI.create(saved.id().toString())).body(saved);
    }

    @PatchMapping("/{interestId}")
    public ResponseEntity<InterestCommonResponse> InterestEdit(
            @PathVariable UUID interestId,
            @RequestBody @Valid InterestUpdateRequest request
    ){
        InterestCommonResponse updated = interestService.editKeywords(interestId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{interestId}")
    public ResponseEntity<Void> InterestDelete(@PathVariable UUID interestId){
        interestService.delete(interestId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{interestId}/subscriptions")
    public ResponseEntity<InterestSubScriptionResponse> subscribe(
        @RequestHeader("Monew-Request-User-ID") UUID userId,
        @PathVariable UUID interestId
    ){
        return ResponseEntity.ok(interestUserService.subscribe(userId, interestId));
    }

    @DeleteMapping("/{interestId}/subscriptions")
    public ResponseEntity<Void> unsubscribe(
        @RequestHeader("Monew-Request-User-ID") UUID userId,
        @PathVariable UUID interestId
    ){
        interestUserService.unSubscribe(userId, interestId);
        return ResponseEntity.noContent().build();
    }
}
