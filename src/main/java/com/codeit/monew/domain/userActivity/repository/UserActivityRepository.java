package com.codeit.monew.domain.userActivity.repository;

import com.codeit.monew.domain.userActivity.dto.UserActivityDto;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserActivityRepository {
    UserActivityDto getUserActivity(UUID userId);
}
