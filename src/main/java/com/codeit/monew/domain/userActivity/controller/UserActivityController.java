package com.codeit.monew.domain.userActivity.controller;

import com.codeit.monew.domain.userActivity.entity.UserActivity;
import com.codeit.monew.domain.userActivity.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-activities")
public class UserActivityController {

    private final UserActivityService userActivityService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserActivity> getUserActivity(@PathVariable UUID userId){
        return ResponseEntity.ok(userActivityService.getByUserId(userId));
    }
}
