package com.codeit.monew.domain.comment.controller.docs;

import com.codeit.monew.domain.comment.dto.request.CommentOrderBy;
import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.request.CommentUpdateRequest;
import com.codeit.monew.domain.comment.dto.request.SortDirection;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.global.dto.ErrorResponse;
import com.codeit.monew.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Comment", description = "댓글 관련 API")
@RequestMapping("/api/comments")
public interface CommentControllerDocs {

    @Operation(
            summary = "댓글 목록 조회",
            description = """
                    특정 기사에 대한 댓글 목록을 조회합니다.
                    정렬 기준(orderBy), 정렬 방향(direction),
                    커서(cursor + after) 기반 페이지네이션을 지원합니다.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = PageResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping
    ResponseEntity<PageResponse<CommentDto>> getComments(@Parameter(description = "기사 ID(UUID)", required = true, example = "d1c17835-6325-40ba-8d72-5e6f5f23a8fc")
            @RequestParam UUID articleId,

                                                         @Parameter(description = "요청 사용자 ID(UUID)", required = true, example = "b285abcc-8e72-4bb4-9b8d-d1ddc93242ff")
            @RequestHeader("Monew-Request-User-ID") UUID userId,

                                                         @Parameter(description = "정렬 기준", example = "createdAt")
            @RequestParam CommentOrderBy orderBy,

                                                         @Parameter(description = "정렬 방향", example = "DESC")
            @RequestParam SortDirection direction,

                                                         @Parameter(description = "커서 값", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @RequestParam(required = false) String cursor,

                                                         @Parameter(description = "이전 기준 시각(ISO-8601)", example = "2026-01-20T10:15:30")
            @RequestParam(required = false) String after,

                                                         @Parameter(description = "페이지 크기", example = "50")
            @RequestParam(defaultValue = "50") int limit
    );

    @Operation(
            summary = "댓글 등록",
            description = "특정 기사에 새로운 댓글을 등록합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "등록 성공",
                            content = @Content(schema = @Schema(implementation = CommentDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "입력값 검증 실패",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping
    ResponseEntity<CommentDto> create(
            @RequestBody CommentRegisterRequest request
    );

    @Operation(
            summary = "댓글 수정",
            description = "기존 댓글의 내용을 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "수정 성공",
                            content = @Content(schema = @Schema(implementation = CommentDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "댓글 정보 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PatchMapping("/{commentId}")
    ResponseEntity<CommentDto> update(
            @Parameter(description = "댓글 ID(UUID)", required = true, example = "bbad74a7-7533-4805-b8f7-78fa910ad44f")
            @PathVariable UUID commentId,

            @Parameter(description = "요청 사용자 ID(UUID)", required = true, example = "bbad74a7-7533-4805-b8f7-78fa910ad44f")
            @RequestHeader("Monew-Request-User-ID") UUID userId,

            @RequestBody CommentUpdateRequest request
    );

    @Operation(
            summary = "댓글 논리 삭제",
            description = "댓글을 논리 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "삭제 성공"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "댓글 정보 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @DeleteMapping("/{commentId}")
    ResponseEntity<Void> delete(
            @Parameter(description = "댓글 ID(UUID)", required = true, example = "bbad74a7-7533-4805-b8f7-78fa910ad44f")
            @PathVariable UUID commentId
    );

    @Operation(
            summary = "댓글 물리 삭제",
            description = "댓글을 DB에서 완전히 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "삭제 성공"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "댓글 정보 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @DeleteMapping("/{commentId}/hard")
    ResponseEntity<Void> deleteHard(
            @Parameter(description = "댓글 ID(UUID)", required = true, example = "bbad74a7-7533-4805-b8f7-78fa910ad44f")
            @PathVariable UUID commentId
    );
}
