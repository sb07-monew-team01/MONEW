package com.codeit.monew.domain.userActivity.service;

import com.codeit.monew.domain.userActivity.dto.UserActivityDto;

import java.util.UUID;

public interface UserActivityService {
    UserActivityDto getByUserId(UUID userId);
}
