package com.codeit.monew.domain.userActivity.controller.docs;

import com.codeit.monew.domain.userActivity.dto.UserActivityDto;
import com.codeit.monew.global.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "UserActivity", description = "사용자 활동 조회 API")
public interface UserActivityControllerDocs {

    @Operation(
            summary = "사용자 활동 조회",
            description = """
                    사용자 ID를 기반으로 해당 사용자의 모든 활동 내역을 조회합니다.
                    활동 내역에는 관심사 구독, 작성한 댓글, 댓글 좋아요, 기사 조회 기록이 포함됩니다.
                    """
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserActivityDto.class)))
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<UserActivityDto> getUserActivity(
            @Parameter(description = "사용자 ID", required = true,
                    example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @PathVariable UUID userId
    );
}
