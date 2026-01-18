package com.codeit.monew.domain.interestuser.service;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.interestuser.repository.InterestUserRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InterestUserServiceImpl implements InterestUserService{
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final InterestUserRepository interestUserRepository;

    @Override
    @Transactional
    public InterestUser subscribe(UUID userId, UUID interestId) {
        User user = userRepository.findById(userId).orElseThrow(
                () ->  new UserNotFoundException(userId));
        Interest interest = interestRepository.findById(interestId).get();
        return interestUserRepository.save(new InterestUser(user, interest));
    }
}
