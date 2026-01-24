package com.codeit.monew.domain.interest.controller;

import com.codeit.monew.domain.interest.controller.docs.InterestControllerDocs;
import com.codeit.monew.domain.interest.dto.request.InterestCreatedRequest;
import com.codeit.monew.domain.interest.dto.request.InterestCursorPageRequest;
import com.codeit.monew.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.monew.domain.interest.dto.response.InterestCommonResponse;
import com.codeit.monew.domain.interest.dto.response.InterestSubscriptionResponse;
import com.codeit.monew.domain.interest.service.InterestService;
import com.codeit.monew.domain.interestuser.service.InterestUserService;
import com.codeit.monew.global.aop.annotation.LogExecution;
import com.codeit.monew.global.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interests")
@LogExecution
public class InterestController implements InterestControllerDocs {
    private final InterestService interestService;
    private final InterestUserService interestUserService;

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<InterestCommonResponse>> interestList(
        @RequestHeader("Monew-Request-User-ID") UUID userId,
        @ParameterObject InterestCursorPageRequest request
    ){
        return ResponseEntity.ok(interestService.getInterests(userId, request));
    }

    @Override
    @PostMapping
    public ResponseEntity<InterestCommonResponse> interestCreate(
            @RequestBody @Valid InterestCreatedRequest request
    ){
        InterestCommonResponse saved = interestService.create(request);
        return ResponseEntity.created(URI.create(saved.id().toString())).body(saved);
    }

    @Override
    @PatchMapping("/{interestId}")
    public ResponseEntity<InterestCommonResponse> interestEdit(
            @PathVariable UUID interestId,
            @RequestBody @Valid InterestUpdateRequest request
    ){
        InterestCommonResponse updated = interestService.editKeywords(interestId, request);
        return ResponseEntity.ok(updated);
    }

    @Override
    @DeleteMapping("/{interestId}")
    public ResponseEntity<Void> interestDelete(@PathVariable UUID interestId){
        interestService.delete(interestId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/{interestId}/subscriptions")
    public ResponseEntity<InterestSubscriptionResponse> subscribe(
        @RequestHeader("Monew-Request-User-ID") UUID userId,
        @PathVariable UUID interestId
    ){
        return ResponseEntity.ok(interestUserService.subscribe(userId, interestId));
    }

    @Override
    @DeleteMapping("/{interestId}/subscriptions")
    public ResponseEntity<Void> unsubscribe(
        @RequestHeader("Monew-Request-User-ID") UUID userId,
        @PathVariable UUID interestId
    ){
        interestUserService.unSubscribe(userId, interestId);
        return ResponseEntity.noContent().build();
    }
}
