package com.codeit.monew.domain.user.service;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserLoginRequest;
import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
import com.codeit.monew.domain.user.dto.request.UserUpdateRequest;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserAlreadyDeletedException;
import com.codeit.monew.domain.user.exception.UserAlreadyExistsException;
import com.codeit.monew.domain.user.exception.UserLoginFailedException;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
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
    public UserDto signUp(UserSignUpRequest dto) {
        Optional<User> byEmail = userRepository.findByEmail(dto.email());
        if (byEmail.isEmpty()) {
            User user = new User(dto.email(), dto.nickname(), dto.password());
            User saved = userRepository.save(user);
            return userMapper.toDto(saved);
        }

        User user = byEmail.get();
        if (user.isDeleted())
            throw new UserAlreadyDeletedException(user);
        throw new UserAlreadyExistsException(user);
    }

    @Override
    public UserDto login(UserLoginRequest dto) {
        User byEmail = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UserNotFoundException(dto.email()));
        if (byEmail.isDeleted())
            throw new UserAlreadyDeletedException(byEmail);
        if (byEmail.getPassword().equals(dto.password()))
            return userMapper.toDto(byEmail);
        throw new UserLoginFailedException(dto.email());
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (user.isDeleted()) {
            throw new UserAlreadyDeletedException(user);
        }
        user.updateDeletedAt();
    }

    @Transactional
    @Override
    public UserDto updateUser(UserUpdateRequest dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(dto.userId()));
        if (user.isDeleted())
            throw new UserAlreadyDeletedException(user);
        user.updateNickname(dto.newNickname());
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void deleteHard(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
    }
}
