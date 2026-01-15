package com.codeit.monew.domain.user.service;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserLoginRequest;
import com.codeit.monew.domain.user.dto.request.UserUpdateRequest;
import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional
    public UserDto signUp(UserSignUpRequest dto) {
        Optional<User> byEmail = userRepository.findByEmail(dto.email());
        if (byEmail.isEmpty()) {
            User user = new User(dto.email(), dto.nickname(), dto.password());
            User saved = userRepository.save(user);
            return userMapper.toDto(saved);
        }

        User user = byEmail.get();
        if (user.isDeleted()) {
            if (user.getDeletedAt().isAfter(LocalDateTime.now().minusDays(7))) {
                throw new UserAlreadyDeletedException(user);
            } else { // 삭제된지 7일이 지났다면 삭제후 재가입 허용
                userRepository.delete(user);
                User saved = userRepository.save(new User(dto.email(), dto.nickname(), dto.password()));
                return userMapper.toDto(saved);
            }
        }
        throw new UserAlreadyExistsException(user);
    }

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
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (user.isDeleted()) {
            throw new UserAlreadyDeletedException(user);
        }
        user.updateDeletedAt();
    }

    @Transactional
    public UserDto updateUser(UserUpdateRequest dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(dto.userId()));
        if (user.isDeleted())
            throw new UserAlreadyDeletedException(user);
        user.updateNickname(dto.newNickname());
        return userMapper.toDto(user);
    }

    @Transactional
    public void deleteHard(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
    }
}
