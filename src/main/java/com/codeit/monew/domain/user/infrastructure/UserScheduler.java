package com.codeit.monew.domain.user.infrastructure;

import com.codeit.monew.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteUser(){
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        userRepository.deleteAllByDeletedAtBefore(threshold);
    }
}
