package com.codeit.monew.domain.user.controller.docs;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserLoginRequest;
import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
import com.codeit.monew.domain.user.dto.request.UserUpdateRequest;
import com.codeit.monew.global.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "사용자 관련 API")
public interface UserControllerDocs {

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다. 이메일, 비밀번호, 닉네임을 입력받아 계정을 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "회원가입 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "400", description = "유효성 검증 실패",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일 또는 닉네임",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<UserDto> signUp(@Valid @RequestBody UserSignUpRequest request);

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다. 성공 시 응답 헤더에 Monew-Request-User-ID를 포함하여 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "400", description = "유효성 검증 실패",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패 (이메일 또는 비밀번호 불일치)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest request);

    @Operation(
            summary = "사용자 정보 수정",
            description = "사용자의 닉네임, 비밀번호 등을 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "400", description = "유효성 검증 실패",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<UserDto> update(
            @Parameter(description = "사용자 ID", required = true,
                    example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request
    );

    @Operation(
            summary = "사용자 삭제 (소프트 삭제)",
            description = "사용자를 소프트 삭제합니다. 실제 데이터는 유지되며 삭제 플래그만 설정됩니다. 본인만 삭제할 수 있습니다."
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "400", description = "유효성 검증 실패",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "권한 없음 (본인이 아님)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<?> deleteSoft(
            @Parameter(description = "요청자 ID", required = true,
                    example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @RequestHeader(name = "Monew-Request-User-ID") UUID loginId,
            @Parameter(description = "삭제할 사용자 ID", required = true,
                    example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @PathVariable UUID userId
    );

    @Operation(
            summary = "사용자 삭제 (하드 삭제)",
            description = "사용자를 하드 삭제합니다. 데이터베이스에서 완전히 삭제됩니다. 본인만 삭제할 수 있습니다."
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "400", description = "유효성 검증 실패",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "권한 없음 (본인이 아님)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<?> deleteHard(
            @Parameter(description = "요청자 ID", required = true,
                    example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @RequestHeader(name = "Monew-Request-User-ID") UUID loginId,
            @Parameter(description = "삭제할 사용자 ID", required = true,
                    example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @PathVariable UUID userId
    );
}
