package com.codeit.monew.domain.interest.controller.docs;

import com.codeit.monew.domain.interest.dto.request.InterestCreatedRequest;
import com.codeit.monew.domain.interest.dto.request.InterestCursorPageRequest;
import com.codeit.monew.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.monew.domain.interest.dto.response.InterestCommonResponse;
import com.codeit.monew.domain.interest.dto.response.InterestSubScriptionResponse;
import com.codeit.monew.global.dto.ErrorResponse;
import com.codeit.monew.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@Tag(name = "Interest", description = "관심사 관련 API")
public interface InterestControllerDocs {
    @Operation(
            summary = "관심사 목록 조회",
            description = "관심사에 대한 목록을 조회합니다. keyword(관심사 이름, 키워드), 정렬 속성(이름, 구독자 수), 정렬 방향(ASC, DESC), 커서(cursor, after) 기반으로 조회할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))),
                    @ApiResponse(responseCode = "404", description = "요청한 리소스(관심사)를 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<PageResponse<InterestCommonResponse>> interestList(
            @Parameter(description = "요청 사용자 ID", required = true, example = "111c88f3-c4f1-4817-a08d-a385daf60000")
            @RequestHeader("Monew-Request-User-ID") UUID userId,

            @ParameterObject InterestCursorPageRequest request
    );

    @Operation(
            summary = "관심사 등록",
            description = "새로운 관심사를 등록합니다. 하나의 관심사에 등록할 수 있는 키워드는 최대 10개입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InterestCommonResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청, (정렬 기준, 파라미터 값 등이 잘못되었습니다.)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "80% 이상 유사한 이름의 관심사는 등록할 수 없습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<InterestCommonResponse> interestCreate(
            @RequestBody @Valid InterestCreatedRequest request
    );

    @Operation(
            summary = "관심사 정보 수정",
            description = "특정 관심사의 키워드를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InterestCommonResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청, 키워드 입력값이 올바르지 않습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "요청한 리소스(관심사)를 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<InterestCommonResponse> interestEdit(
            @Parameter(description = "관심사 ID", required = true, example = "111c88f3-c4f1-4817-a08d-a385daf60000")
            @PathVariable UUID interestId,

            @RequestBody @Valid InterestUpdateRequest request
    );


    @Operation(
            summary = "관심사 물리 삭제",
            description = "관심사를 물리적으로 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "논리 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "요청한 리소스(관심사)를 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<Void> interestDelete(
            @Parameter(description = "관심사 ID", required = true, example = "111c88f3-c4f1-4817-a08d-a385daf60000")
            @PathVariable UUID interestId);


    @Operation(
            summary = "관심사 구독",
            description = "관심사를 구독합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "구독 성공하였습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InterestSubScriptionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "요청한 리소스(관심사)를 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<InterestSubScriptionResponse> subscribe(
            @Parameter(description = "요청 사용자 ID", required = true, example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @RequestHeader("Monew-Request-User-ID") UUID userId,

            @Parameter(description = "관심사 ID", required = true, example = "111c88f3-c4f1-4817-a08d-a385daf60000")
            @PathVariable UUID interestId
    );


    @Operation(
            summary = "관심사 구독 취소",
            description = "구독중인 관심사를 취소합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "구독 취소를 성공하였습니다."),
                    @ApiResponse(responseCode = "404", description = "요청한 리소스(관심사)를 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<Void> unsubscribe(
            @Parameter(description = "요청 사용자 ID", required = true, example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @RequestHeader("Monew-Request-User-ID") UUID userId,

            @Parameter(description = "관심사 ID", required = true, example = "111c88f3-c4f1-4817-a08d-a385daf60000")
            @PathVariable UUID interestId
    );
}
