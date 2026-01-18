package com.codeit.monew.domain.user.controller;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserLoginRequest;
import com.codeit.monew.domain.user.dto.request.UserNicknameUpdateRequest;
import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
import com.codeit.monew.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> signUp(@Valid @RequestBody UserSignUpRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest request){
        return ResponseEntity.ok(userService.login(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> modify(@Valid @PathVariable UUID id, @Valid @RequestBody UserNicknameUpdateRequest request){
        return ResponseEntity.ok(userService.update(id, request));
    }
}
