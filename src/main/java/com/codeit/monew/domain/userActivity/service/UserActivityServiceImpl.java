package com.codeit.monew.domain.userActivity.service;

import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.userActivity.dto.UserActivityDto;
import com.codeit.monew.domain.userActivity.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActivityServiceImpl implements UserActivityService {

    private final UserActivityRepository userActivityRepository;

    @Override
    public UserActivityDto getByUserId(UUID userId) {
        UserActivityDto userActivity = userActivityRepository.getUserActivity(userId);
        if (userActivity.id() == null) {
            throw new UserNotFoundException(userId);
        }
        return userActivity;
    }
}
