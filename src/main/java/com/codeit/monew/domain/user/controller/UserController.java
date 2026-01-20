package com.codeit.monew.domain.user.controller;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserLoginRequest;
import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
import com.codeit.monew.domain.user.dto.request.UserUpdateRequest;
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
        UserDto response = userService.login(request);
        return ResponseEntity.ok()
                .header("Monew-Request-User-ID", response.id().toString())
                .body(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@RequestHeader(name = "Monew-Request-User-ID") UUID loginId, @PathVariable UUID userId, @Valid @RequestBody UserUpdateRequest request){
        return ResponseEntity.ok(userService.update(loginId, userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteSoft(@RequestHeader(name = "Monew-Request-User-ID") UUID loginId, @PathVariable UUID userId){
        userService.delete(loginId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/hard")
    public ResponseEntity<?> deleteHard(@RequestHeader(name = "Monew-Request-User-ID") UUID loginId, @PathVariable UUID userId){
        userService.deleteHard(loginId, userId);
        return ResponseEntity.noContent().build();
    }
}
