package com.codeit.monew.domain.interest.unit.dto;

import com.codeit.monew.domain.interest.dto.InterestCursorQuery;
import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.SortDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class InterestCursorQueryTest {
    @Nested
    @DisplayName("기본값 셋팅")
    class DefaultValueTest{
        @Test
        @DisplayName("성공: null 입력 시 기본값 설정이 잘 된다")
        void defaultValues(){
            // given
            InterestOrderBy orderBy = null;
            SortDirection direction = null;
            Integer limit = null;

            // when
            InterestCursorQuery query = new InterestCursorQuery(
                    orderBy,
                    direction,
                    null,
                    null,
                    null,
                    limit,
                    null
            );

            // then
            assertThat(query.orderBy()).isEqualTo(InterestOrderBy.NAME);
            assertThat(query.direction()).isEqualTo(SortDirection.ASC);
            assertThat(query.limit()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("정렬 validate 테스트")
    class ValidateTest{
        @Test
        @DisplayName("정상적인 NAME 정렬 쿼리는 validate 통과한다.")
        void validateNameCursorSuccess() {
            // given
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.ASC,
                    "cursorName",
                    null,
                    null,
                    10,
                    null
            );


            // when & then
            assertThatCode(query::validate)
                    .doesNotThrowAnyException(); // 예외가 발생하지 않아야 함
        }

        @Test
        @DisplayName("NAME 정렬에서 subscriberCountCursor 사용 시 예외 발생")
        void validateNameCursorFail() {
            // given
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.ASC,
                    null,
                    5L,
                    null,
                    10,
                    null
            );

            // when & then
            assertThatThrownBy(query::validate)
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("정상적인 SUBSCRIBER_COUNT 정렬 쿼리는 validate 통과")
        void validateSubscriberCountCursorSuccess() {
            // given
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBER_COUNT,
                    SortDirection.DESC,
                    null,
                    10L,
                    null,
                    10,
                    null
            );

            // when & then
            query.validate(); // 예외 없음
        }

        @Test
        @DisplayName("SUBSCRIBER_COUNT 정렬에서 nameCursor 사용 시 예외 발생")
        void validateSubscriberCountCursorFail() {
            // given
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBER_COUNT,
                    SortDirection.DESC,
                    "nameCursor",
                    null,
                    null,
                    10,
                    null
            );

            // when & then
            assertThatThrownBy(query::validate)
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
