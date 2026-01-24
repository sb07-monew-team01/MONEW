package com.codeit.monew.domain.commentuserlike.controller.docs;

import com.codeit.monew.domain.commentuserlike.dto.CommentUserLikeDto;
import com.codeit.monew.global.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Comment Like", description = "댓글 좋아요 API")
@RequestMapping("/api/comments/{commentId}/comment-likes")
public interface CommentUserLikeControllerDocs {
    @Operation(
            summary = "댓글 좋아요",
            description = "특정 댓글에 좋아요를 등록합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "좋아요 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CommentUserLikeDto.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (이미 좋아요를 누른 경우 등)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "댓글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping
    ResponseEntity<CommentUserLikeDto> like(
            @Parameter(description = "댓글 ID(UUID)", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID commentId,

            @Parameter(description = "요청 사용자 ID(UUID)", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @RequestHeader("Monew-Request-User-ID") UUID userId
    );

    @Operation(
            summary = "댓글 좋아요 취소",
            description = "특정 댓글에 대한 좋아요를 취소합니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "좋아요 취소 성공"
    )
    @ApiResponse(
            responseCode = "404",
            description = "댓글 또는 좋아요 정보를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @DeleteMapping
    ResponseEntity<Void> unlike(
            @Parameter(description = "댓글 ID(UUID)", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID commentId,

            @Parameter(description = "요청 사용자 ID(UUID)", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @RequestHeader("Monew-Request-User-ID") UUID userId
    );
}
