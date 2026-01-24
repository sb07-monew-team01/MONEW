package com.codeit.monew.domain.user.service;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.*;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.*;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.domain.user.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto signUp(UserSignUpRequest request) {
        Optional<User> byEmail = userRepository.findByEmail(request.email());
        if (byEmail.isEmpty()) {
            User user = new User(request.email(), request.nickname(), request.password());
            User saved = userRepository.save(user);
            return userMapper.toDto(saved);
        }

        User user = byEmail.get();
        if (user.isDeleted())
            throw new UserAlreadyDeletedException(user);
        throw new UserAlreadyExistsException(user);
    }

    @Override
    public UserDto login(UserLoginRequest request) {
        User byEmail = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));
        if (byEmail.isDeleted())
            throw new UserAlreadyDeletedException(byEmail);
        if (byEmail.getPassword().equals(request.password()))
            return userMapper.toDto(byEmail);
        throw new UserLoginFailedException(request.email());
    }

    @Transactional
    @Override
    public void delete(UUID loginId, UUID deleteId) {
        if (!loginId.equals(deleteId))
            throw new UserNotAuthorizedException(loginId, deleteId);
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new UserNotFoundException(loginId));
        if (user.isDeleted()) {
            throw new UserAlreadyDeletedException(user);
        }
        user.updateDeletedAt();
    }

    @Transactional
    @Override
    public void deleteHard(UUID loginId, UUID deleteId) {
        if (!loginId.equals(deleteId))
            throw new UserNotAuthorizedException(loginId, deleteId);
        User user = userRepository.findById(deleteId)
                .orElseThrow(() -> new UserNotFoundException(deleteId));
        userRepository.delete(user);
    }

    @Override
    public UserDto update(UUID requestId, UserUpdateRequest request) {
        User user = userRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException(requestId));
        if (user.isDeleted())
            throw new UserAlreadyDeletedException(user);
        user.updateNickname(request.nickname());
        return userMapper.toDto(user);
    }
}
