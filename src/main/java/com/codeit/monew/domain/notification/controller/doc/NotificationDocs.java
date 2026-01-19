package com.codeit.monew.domain.notification.controller.doc;

import com.codeit.monew.domain.notification.dto.request.NotificationPageQuery;
import com.codeit.monew.domain.notification.dto.response.NotificationDto;
import com.codeit.monew.global.dto.ErrorResponse;
import com.codeit.monew.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notifications", description = "알림 조회/확인 API")
@RequestMapping("/api/notifications")
public interface NotificationDocs {

    @Operation(
            summary = "확인하지 않은 알림 목록 조회",
            description = """
                    확인하지 않은 알림만 조회합니다.
                    createdAt(커서) + after(UUID) 기반 커서 페이지네이션을 사용합니다.
                    limit 미지정 시 기본값은 50입니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                {
                                  "content": [
                                    {
                                      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                      "createdAt": "2026-01-19T10:51:10.737Z",
                                      "updatedAt": "2026-01-19T10:51:10.737Z",
                                      "confirmed": false,
                                      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                      "content": "string",
                                      "resourceType": "interest",
                                      "resourceId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                    },
                                    {
                                      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                      "createdAt": "2026-02-19T10:51:10.737Z",
                                      "updatedAt": "2026-02-19T10:51:10.737Z",
                                      "confirmed": false,
                                      "userId": "2fa85f64-5717-4562-b3fc-2c963f66afa6",
                                      "content": "string2",
                                      "resourceType": "interest2",
                                      "resourceId": "2fa85f64-5717-4562-b3fc-2c963f66afa6"
                                    }
                                  ],
                                  "nextCursor": "2026-01-19T17:06:08.299_3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                  "nextAfter": "2025-04-06T15:04:05.000Z",
                                  "size": 2,
                                  "totalElements": 100,
                                  "hasNext": true
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 파라미터 검증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "InvalidArgument",
                                    value = """
                                {
                                  "timestamp": "2026-01-19T18:03:07.0758226",
                                  "code": "INVALID_ARGUMENT",
                                  "message": "올바른 형식이 아닙니다.",
                                  "details": {
                                    "message": "형식에 어긋납니다",
                                    "field": "notificationPageQuery"
                                  },
                                  "exceptionType": "MethodArgumentNotValidException",
                                  "status": 400
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류,헤더 누락",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "INTERNAL_SERVER_ERROR",
                                    value = """
                            {
                                "timestamp": "2026-01-19T19:35:40.8514002",
                                "code": "INTERNAL_SERVER_ERROR",
                                "message": "서버 내부에 오류가 발생했습니다.",
                                "details": {
                                    "message": "Required request header 'Monew-Request-User-ID' for method parameter type UUID is not present"
                                },
                                "exceptionType": "MissingRequestHeaderException",
                                "status": 500
                            }
                            """
                            )
                    )
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<NotificationDto>> getNotification(
            @ParameterObject @Valid @ModelAttribute NotificationPageQuery request,
            @Parameter(
                    name = "Monew-Request-User-ID",
                    description = "요청 사용자 ID(UUID). 헤더로 전달합니다.",
                    required = true
            )
            @RequestHeader("Monew-Request-User-ID") UUID userId
    );

    @Operation(
            summary = "알림 확인",
            description = """
                    요청 사용자의 확인하지 않은 알림을 확인(confirmed=true) 처리합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = NotificationDto.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                {
                                  "content": [
                                    {
                                      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                      "createdAt": "2026-01-19T10:51:10.737Z",
                                      "updatedAt": "2026-01-19T10:51:10.737Z",
                                      "confirmed": true,
                                      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                      "content": "string",
                                      "resourceType": "interest",
                                      "resourceId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                    }
                                  ]
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (알림 ID검증 실패)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "NOTIFICATION_NOT_FOUND",
                                    value = """
                                    {
                                        "timestamp": "2026-01-19T20:07:24.7859309",
                                        "code": "NOTIFICATION_NOT_FOUND",
                                        "message": "해당 알림을 찾을 수 없습니다.",
                                        "details": {
                                            "notificationID": "7a187ac2-87f7-44d3-a8fb-b1936f9c487b",
                                            "class": "NotificationException"
                                        },
                                        "exceptionType": "NotificationNotFoundException",
                                        "status": 400
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류,헤더 누락",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "INTERNAL_SERVER_ERROR",
                                    value = """
                            {
                                "timestamp": "2026-01-19T19:35:40.8514002",
                                "code": "INTERNAL_SERVER_ERROR",
                                "message": "서버 내부에 오류가 발생했습니다.",
                                "details": {
                                    "message": "Required request header 'Monew-Request-User-ID' for method parameter type UUID is not present"
                                },
                                "exceptionType": "MissingRequestHeaderException",
                                "status": 500
                            }
                            """
                            )
                    )
            )
    })
    @PatchMapping("/{notificationId}")
    ResponseEntity<NotificationDto> confirmNotification(
            @Parameter(description = "알림 UUID")
            @PathVariable UUID notificationId,
            @Parameter(description = "요청 사용자 ID(UUID). 헤더로 전달합니다.")
            @RequestHeader("Monew-Request-User-ID") UUID userId
    );


    @Operation(
            summary = "알림 전체 확인",
            description = "요청 사용자의 확인하지 않은 알림을 모두 확인(confirmed=true) 처리합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = NotificationDto.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                {
                                  "content": [
                                    {
                                      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                      "createdAt": "2026-01-19T10:51:10.737Z",
                                      "updatedAt": "2026-01-19T10:51:10.737Z",
                                      "confirmed": true,
                                      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                      "content": "string",
                                      "resourceType": "interest",
                                      "resourceId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                    },
                                                                        {
                                      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                      "createdAt": "2026-01-19T10:51:10.737Z",
                                      "updatedAt": "2026-01-19T10:51:10.737Z",
                                      "confirmed": true,
                                      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                      "content": "string",
                                      "resourceType": "interest",
                                      "resourceId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                    }
                                  ]
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류,헤더 누락",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "INTERNAL_SERVER_ERROR",
                                    value = """
                            {
                                "timestamp": "2026-01-19T19:35:40.8514002",
                                "code": "INTERNAL_SERVER_ERROR",
                                "message": "서버 내부에 오류가 발생했습니다.",
                                "details": {
                                    "message": "Required request header 'Monew-Request-User-ID' for method parameter type UUID is not present"
                                },
                                "exceptionType": "MissingRequestHeaderException",
                                "status": 500
                            }
                            """
                            )
                    )
            )
    })
    @PatchMapping
    ResponseEntity<List<NotificationDto>> confirmAllNotification(
            @Parameter(description = "요청 사용자 ID(UUID). 헤더로 전달합니다.")
            @RequestHeader("Monew-Request-User-ID") UUID userId
    );
}
