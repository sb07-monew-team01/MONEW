package com.codeit.monew.domain.interest.unit.dto;

import com.codeit.monew.domain.interest.dto.query.InterestCursorQuery;
import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.SortDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class InterestCursorQueryTest {
    @Nested
    @DisplayName("정렬 validate 테스트")
    class ValidateTest{
        @Nested
        @DisplayName("정렬 조건에 따른 생성자 검증 테스트")
        class ConstructorValidationTest {

            @Test
            @DisplayName("NAME 정렬 + nameCursor 사용 시 정상 생성된다")
            void createNameOrderQuerySuccess() {
                // when & then
                assertThatCode(() ->
                        new InterestCursorQuery(
                                InterestOrderBy.NAME,
                                SortDirection.ASC,
                                "cursorName",
                                null,
                                null,
                                10,
                                null
                        )
                ).doesNotThrowAnyException();
            }

            @Test
            @DisplayName("NAME 정렬에서 subscriberCountCursor 사용 시 예외 발생")
            void createNameOrderQueryFail() {
                // when & then
                assertThatThrownBy(() ->
                        new InterestCursorQuery(
                                InterestOrderBy.NAME,
                                SortDirection.ASC,
                                null,
                                5L,
                                null,
                                10,
                                null
                        )
                ).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("SUBSCRIBERCOUNT 정렬 + subscriberCountCursor 사용 시 정상 생성된다")
            void createSubscriberCountOrderQuerySuccess() {
                // when & then
                assertThatCode(() ->
                        new InterestCursorQuery(
                                InterestOrderBy.SUBSCRIBERCOUNT,
                                SortDirection.DESC,
                                null,
                                10L,
                                null,
                                10,
                                null
                        )
                ).doesNotThrowAnyException();
            }

            @Test
            @DisplayName("SUBSCRIBERCOUNT 정렬에서 nameCursor 사용 시 예외 발생")
            void createSubscriberCountOrderQueryFail() {
                // when & then
                assertThatThrownBy(() ->
                        new InterestCursorQuery(
                                InterestOrderBy.SUBSCRIBERCOUNT,
                                SortDirection.DESC,
                                "nameCursor",
                                null,
                                null,
                                10,
                                null
                        )
                ).isInstanceOf(IllegalArgumentException.class);
            }
        }
    }
}
