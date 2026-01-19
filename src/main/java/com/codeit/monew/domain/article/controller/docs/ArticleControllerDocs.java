package com.codeit.monew.domain.article.controller.docs;

import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;
import com.codeit.monew.global.dto.ErrorResponse;
import com.codeit.monew.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@Tag(name = "Article", description = "뉴스 기사 관련 API")
public interface ArticleControllerDocs {
    @Operation(
            summary = "뉴스 기사 단건 조회",
            description = "뉴스기사 ID(articleId)와 요청자 ID(Monew-Request-User-ID, Request Header)를 기반으로 뉴스 기사 단건을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArticleDto.class))),
                    @ApiResponse(responseCode = "404", description = "요청한 리소스(기사 또는 사용자)를 찾을 수 없음",
                            content = @Content(mediaType = "application/json",  schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(mediaType = "application/json",  schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<ArticleDto> articleDetails(
            @Parameter(description = "뉴스 기사 ID", required = true, example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @PathVariable UUID articleId,

            @Parameter(description = "요청자 사용자 ID", required = true, example = "111c88f3-c4f1-4817-a08d-a385daf60000")
            @RequestHeader("Monew-Request-User-ID") UUID userId
    );

    @Operation(
            summary = "뉴스 기사 목록 조회",
            description = "조건에 맞는 뉴스 기사를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json",  schema = @Schema(implementation = PageResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청, (정렬 기준, 파라미터 값 등이 잘못되었습니다.)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<PageResponse<ArticleDto>> articleList(
            @ParameterObject ArticleSearchRequest request,

            @Parameter(description = "요청자 사용자 ID", required = true, example = "111c88f3-c4f1-4817-a08d-a385daf60000")
            @RequestHeader("Monew-Request-User-ID") UUID userId
    );


    @Operation(
            summary = "기사 뷰 등록",
            description = "기사 뷰를 등록합니다. 뷰로 등록된 기사는 활동내역 탭, '최근 본 기사'에서 확인할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "기사 뷰 등록 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArticleViewDto.class))),
                    @ApiResponse(responseCode = "404", description = "요청한 리소스(기사 또는 사용자)를 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<ArticleViewDto> articleViewAdd(
            @Parameter(description = "뉴스 기사 ID", required = true, example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @PathVariable UUID articleId,

            @Parameter(description = "요청자 사용자 ID", required = true, example = "111c88f3-c4f1-4817-a08d-a385daf60000")
            @RequestHeader("Monew-Request-User-ID") UUID userId);

    @Operation(
            summary = "기사 출처 목록 조회",
            description = "기사의 출처 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "출처 목록 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArticleSource.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ArticleSource.class))))
            }
    )
    ResponseEntity<List<ArticleSource>> sourceList();

    @Operation(
            summary = "뉴스 기사 논리 삭제",
            description = "뉴스 기사를 논리적으로 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "논리 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "요청한 리소스(기사)를 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<Void> deleteArticle(
            @Parameter(description = "뉴스 기사 ID", required = true, example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @PathVariable("articleId") UUID articleId);

    @Operation(
            summary = "뉴스 기사 물리 삭제",
            description = "뉴스 기사를 물리적으로 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "물리 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "요청한 리소스(기사)를 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<Void> deleteArticleHard(
            @Parameter(description = "뉴스 기사 ID", required = true, example = "302c88f3-c4f1-4817-a08d-a385daf6944d")
            @PathVariable("articleId") UUID articleId);


}
