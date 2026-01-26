package com.codeit.monew.domain.userActivity.repository;

import com.codeit.monew.domain.userActivity.entity.UserActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserActivityRepository extends MongoRepository<UserActivity, String> {
    Optional<UserActivity> getByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
