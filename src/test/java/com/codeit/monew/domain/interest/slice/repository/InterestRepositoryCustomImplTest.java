package com.codeit.monew.domain.interest.slice.repository;

import com.codeit.monew.domain.interest.dto.query.InterestCursorQuery;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.interest.repository.InterestRepositoryCustomImpl;
import com.codeit.monew.domain.interest.util.InterestTestUtils;
import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.SortDirection;
import com.codeit.monew.global.config.TestJpaAuditing;
import com.codeit.monew.global.config.TestQueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import({TestQueryDslConfig.class, TestJpaAuditing.class})
@ActiveProfiles("test")
public class InterestRepositoryCustomImplTest {
    @Autowired
    InterestRepositoryCustomImpl interestRepositoryCustom;

    @Autowired
    InterestRepository interestRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("비어있는 리스트 조회 - 오름차순")
    class emptyListViewAsc{
        @Test
        @DisplayName(
            """
                정렬 기준이 이름일 때,
                content는 빈 배열
                hasNext 는 false
                을 반환한다.
            """
        )
        void sortByName(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, 0);
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.ASC,
                    null,
                    null,
                    null,
                    10,
                    null
            );

            // when
            Slice<Interest> result = interestRepositoryCustom.findAllByCursor(query);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.hasNext()).isFalse();
        }

        @Test
        @DisplayName(
            """
                정렬 기준이 구독자 수일 때,
                content는 빈 배열
                hasNext 는 false
                을 반환한다.
            """
        )
        void sortBySubscriberCount(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, 0);
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBERCOUNT,
                    SortDirection.ASC,
                    null,
                    null,
                    null,
                    10,
                    null
            );

            // when
            Slice<Interest> result = interestRepositoryCustom.findAllByCursor(query);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.hasNext()).isFalse();
        }
    }

    @Nested
    @DisplayName("비어있는 리스트 조회 - 내림차순")
    class emptyListViewDesc{
        @Test
        @DisplayName(
            """
                정렬 기준이 이름일 때,
                content는 빈 배열
                hasNext 는 false
                을 반환한다.
            """
        )
        void sortByName(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, 0);
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.DESC,
                    null,
                    null,
                    null,
                    10,
                    null
            );

            // when
            Slice<Interest> result = interestRepositoryCustom.findAllByCursor(query);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.hasNext()).isFalse();
        }

        @Test
        @DisplayName(
                """
                    정렬 기준이 구독자 수일 때,
                    content는 빈 배열
                    hasNext 는 false
                    을 반환한다.
                """
        )
        void sortBySubscriberCount(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, 0);
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBERCOUNT,
                    SortDirection.DESC,
                    null,
                    null,
                    null,
                    10,
                    null
            );

            // when
            Slice<Interest> result = interestRepositoryCustom.findAllByCursor(query);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.hasNext()).isFalse();
        }
    }

    @Nested
    @DisplayName("limit 미만의 항목 수를 가진 리스트를 조회 - 오름차순")
    class smallListViewAsc{
        private final int pageSize = 20;

        @Test
        @DisplayName("""
            정렬 기준이 이름일 때,
            정렬이 이름 오름차순으로 잘 되어있고,
            content는 모든 항목
            hasNext 는 false
            을 반환한다.
        """)
        void sortByName(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, 10);
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.ASC,
                    null,
                    null,
                    null,
                    pageSize,
                    null
            );

            // when
            Slice<Interest> slice = interestRepositoryCustom.findAllByCursor(query);

            // then
            assertThat(slice.getContent())
                    .isSortedAccordingTo(
                            Comparator.comparing(Interest::getName)
                                    .thenComparing(Interest::getCreatedAt)
                                    .thenComparing(Interest::getId)
                    );
            assertThat(slice.getContent()).hasSize(10);
            assertThat(slice.hasNext()).isFalse();
        }

        @Test
        @DisplayName("""
            정렬 기준이 구독자 수일 때,
            정렬이 구독자 수 오름차순으로 잘 되어있고,
            content는 모든 항목
            hasNext 는 false
            을 반환한다.
        """)
        void sortBySubscriberCount(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, 10);
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBERCOUNT,
                    SortDirection.ASC,
                    null,
                    null,
                    null,
                    pageSize,
                    null
            );

            // when
            Slice<Interest> slice = interestRepositoryCustom.findAllByCursor(query);

            // then
            assertThat(slice.getContent())
                    .isSortedAccordingTo(
                            Comparator.comparing(Interest::getSubscriberCount)
                                    .thenComparing(Interest::getCreatedAt)
                                    .thenComparing(Interest::getId)
                    );
            assertThat(slice.getContent()).hasSize(10);
            assertThat(slice.hasNext()).isFalse();
        }
    }

    @Nested
    @DisplayName("limit 미만의 항목 수를 가진 리스트를 조회 - 내림차순")
    class smallListViewDesc{
        private final int pageSize = 20;

        @Test
        @DisplayName("""
            정렬 기준이 이름일 때,
            정렬이 이름 내림차순으로 잘 되어있고,
            content는 모든 항목
            hasNext 는 false
            을 반환한다.
        """)
        void sortByName(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, 10);
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.DESC,
                    null,
                    null,
                    null,
                    pageSize,
                    null
            );

            // when
            Slice<Interest> slice = interestRepositoryCustom.findAllByCursor(query);

            // then
            assertThat(slice.getContent())
                    .isSortedAccordingTo(
                            Comparator.comparing(Interest::getName, Comparator.reverseOrder())
                                    .thenComparing(Interest::getCreatedAt, Comparator.reverseOrder())
                                    .thenComparing(Interest::getId, Comparator.reverseOrder())
                    );
            assertThat(slice.getContent()).hasSize(10);
            assertThat(slice.hasNext()).isFalse();
        }

        @Test
        @DisplayName("""
            정렬 기준이 구독자 수일 때,
            정렬이 이름 내림차순으로 잘 되어있고,
            content는 모든 항목
            hasNext 는 false
            을 반환한다.
        """)
        void sortBySubscriberCount(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, 10);
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBERCOUNT,
                    SortDirection.DESC,
                    null,
                    null,
                    null,
                    pageSize,
                    null
            );

            // when
            Slice<Interest> slice = interestRepositoryCustom.findAllByCursor(query);

            // then
            assertThat(slice.getContent())
                    .isSortedAccordingTo(
                            Comparator.comparing(Interest::getSubscriberCount, Comparator.reverseOrder())
                                    .thenComparing(Interest::getCreatedAt, Comparator.reverseOrder())
                                    .thenComparing(Interest::getId, Comparator.reverseOrder())
                    );
            assertThat(slice.getContent()).hasSize(10);
            assertThat(slice.hasNext()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("limit 이상의 항목 수를 가진 리스트를 조회 - 오름차순")
    class largeListViewAsc{
        private final int pageSize = 10;
        private final int totalCount = 26;

        @Test
        @DisplayName("""
            정렬 기준이 이름일 때,
            각 페이지의 정렬이 이름 오름차순으로 잘 되어있고,
            첫 번째 첫 요소 < 중간 페이지 첫 요소 < 마지막 페이지 첫 요소이며
            첫번째 페이지는
                content는 일부 항목
                hasNext 는 true
            중간 페이지는
                content는 일부 항목
                hasNext 는 true
            마지막 페이지는
                content는 남은 항목
                hasNext 는 false
            을 반환한다.
        """)
        void sortByName(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, totalCount);

            // [Page 1]
            // given
            InterestCursorQuery query1 = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.ASC,
                    null,
                    null,
                    null,
                    pageSize,
                    null
            );

            // when
            Slice<Interest> page1 = interestRepositoryCustom.findAllByCursor(query1);

            // [Page 2]
            // given
            InterestCursorQuery query2 = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.ASC,
                    page1.getContent().get(page1.getContent().size() - 1).getName(),
                    null,
                    page1.getContent().get(page1.getContent().size() - 1).getCreatedAt(),
                    pageSize,
                    null
            );
            // when
            Slice<Interest> page2 = interestRepositoryCustom.findAllByCursor(query2);

            // [Page 3]
            // given
            InterestCursorQuery query3 = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.ASC,
                    page2.getContent().get(page2.getContent().size() - 1).getName(),
                    null,
                    page2.getContent().get(page2.getContent().size() - 1).getCreatedAt(),
                    pageSize,
                    null
            );
            // when
            Slice<Interest> page3 = interestRepositoryCustom.findAllByCursor(query3);

            // then
            // 각 페이지는 이름 오름차순으로 정렬이 잘 되어있다.
            assertThat(page1.getContent())
                    .isSortedAccordingTo(Comparator.comparing(Interest::getName)
                            .thenComparing(Interest::getCreatedAt)
                            .thenComparing(Interest::getId));
            assertThat(page2.getContent())
                    .isSortedAccordingTo(Comparator.comparing(Interest::getName)
                            .thenComparing(Interest::getCreatedAt)
                            .thenComparing(Interest::getId));
            assertThat(page3.getContent())
                    .isSortedAccordingTo(Comparator.comparing(Interest::getName)
                            .thenComparing(Interest::getCreatedAt)
                            .thenComparing(Interest::getId));

            // 페이지들 간의 첫 요소 비교
            assertThat(page1.getContent().get(0).getName())
                    .isLessThanOrEqualTo(page2.getContent().get(0).getName());
            assertThat(page2.getContent().get(0).getName())
                    .isLessThanOrEqualTo(page3.getContent().get(0).getName());

            // 각 페이지가 있는지 여부 검증
            assertThat(page1.hasNext()).isTrue();
            assertThat(page2.hasNext()).isTrue();
            assertThat(page3.hasNext()).isFalse();

            // 각 페이지 요소 수 검증
            assertThat(page1.getContent()).hasSize(pageSize);
            assertThat(page2.getContent()).hasSize(pageSize);
            assertThat(page3.getContent()).hasSize(totalCount % pageSize);
        }

        @Test
        @DisplayName("""
            정렬 기준이 구독자 수일 때,
            각 페이지의 정렬이 구독자 수 오름차순으로 잘 되어있고,
            첫 번째 첫 요소 < 중간 페이지 첫 요소 < 마지막 페이지 첫 요소이며
            첫번째 페이지는
                content는 일부 항목
                hasNext 는 true
            중간 페이지는
                content는 일부 항목
                hasNext 는 true
            마지막 페이지는
                content는 남은 항목
                hasNext 는 false
            을 반환한다.
        """)
        void sortBySubscriberCount(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, totalCount);

            // [Page 1]
            // given
            InterestCursorQuery query1 = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBERCOUNT,
                    SortDirection.ASC,
                    null,
                    null,
                    null,
                    pageSize,
                    null
            );

            // when
            Slice<Interest> page1 = interestRepositoryCustom.findAllByCursor(query1);

            // [Page 2]
            // given
            InterestCursorQuery query2 = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBERCOUNT,
                    SortDirection.ASC,
                    null,
                    page1.getContent().get(page1.getContent().size() - 1).getSubscriberCount(),
                    page1.getContent().get(page1.getContent().size() - 1).getCreatedAt(),
                    pageSize,
                    null
            );
            // when
            Slice<Interest> page2 = interestRepositoryCustom.findAllByCursor(query2);

            // [Page 3]
            // given
            InterestCursorQuery query3 = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBERCOUNT,
                    SortDirection.ASC,
                    null,
                    page2.getContent().get(page2.getContent().size() - 1).getSubscriberCount(),
                    page2.getContent().get(page2.getContent().size() - 1).getCreatedAt(),
                    pageSize,
                    null
            );
            // when
            Slice<Interest> page3 = interestRepositoryCustom.findAllByCursor(query3);

            // then
            // 각 페이지는 이름 오름차순으로 정렬이 잘 되어있다.
            assertThat(page1.getContent())
                    .isSortedAccordingTo(Comparator.comparing(Interest::getSubscriberCount)
                            .thenComparing(Interest::getCreatedAt)
                            .thenComparing(Interest::getId));
            assertThat(page2.getContent())
                    .isSortedAccordingTo(Comparator.comparing(Interest::getSubscriberCount)
                            .thenComparing(Interest::getCreatedAt)
                            .thenComparing(Interest::getId));
            assertThat(page3.getContent())
                    .isSortedAccordingTo(Comparator.comparing(Interest::getSubscriberCount)
                            .thenComparing(Interest::getCreatedAt)
                            .thenComparing(Interest::getId));

            // 페이지들 간의 첫 요소 비교
            assertThat(page1.getContent().get(0).getSubscriberCount())
                    .isLessThanOrEqualTo(page2.getContent().get(0).getSubscriberCount());
            assertThat(page2.getContent().get(0).getSubscriberCount())
                    .isLessThanOrEqualTo(page3.getContent().get(0).getSubscriberCount());

            // 각 페이지가 있는지 여부 검증
            assertThat(page1.hasNext()).isTrue();
            assertThat(page2.hasNext()).isTrue();
            assertThat(page3.hasNext()).isFalse();

            // 각 페이지 요소 수 검증
            assertThat(page1.getContent()).hasSize(pageSize);
            assertThat(page2.getContent()).hasSize(pageSize);
            assertThat(page3.getContent()).hasSize(totalCount % pageSize);
        }
    }

    @Nested
    @DisplayName("limit 이상의 항목 수를 가진 리스트를 조회 - 내림차순")
    class largeListViewDesc{
        private final int pageSize = 10;
        private final int totalCount = 26;

        @Test
        @DisplayName("""
            정렬 기준이 이름일 때,
            각 페이지의 정렬이 이름 내림차순으로 잘 되어있고,
            첫 번째 첫 요소 > 중간 페이지 첫 요소 > 마지막 페이지 첫 요소이며
            첫번째 페이지는
                content는 일부 항목
                hasNext 는 true
            중간 페이지는
                content는 일부 항목
                hasNext 는 true
            마지막 페이지는
                content는 남은 항목
                hasNext 는 false
            을 반환한다.
        """)
        void sortByName(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, totalCount);

            // [Page 1]
            // given
            InterestCursorQuery query1 = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.DESC,
                    null,
                    null,
                    null,
                    pageSize,
                    null
            );
            // when
            Slice<Interest> page1 = interestRepositoryCustom.findAllByCursor(query1);

            // [Page 2]
            // given
            InterestCursorQuery query2 = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.DESC,
                    page1.getContent().get(page1.getContent().size() - 1).getName(),
                    null,
                    page1.getContent().get(page1.getContent().size() - 1).getCreatedAt(),
                    pageSize,
                    null
            );

            //when
            Slice<Interest> page2 = interestRepositoryCustom.findAllByCursor(query2);

            // [Page 3]
            // given
            InterestCursorQuery query3 = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.DESC,
                    page2.getContent().get(page2.getContent().size() - 1).getName(),
                    null,
                    page2.getContent().get(page2.getContent().size() - 1).getCreatedAt(),
                    pageSize,
                    null
            );
            // when
            Slice<Interest> page3 = interestRepositoryCustom.findAllByCursor(query3);

            // then
            // 각 페이지는 이름 내림차순으로 정렬이 잘 되어있다.
            assertThat(page1.getContent())
                    .isSortedAccordingTo(Comparator.comparing(Interest::getName).reversed()
                            .thenComparing(Interest::getCreatedAt).reversed()
                            .thenComparing(Interest::getId).reversed());
            assertThat(page2.getContent())
                    .isSortedAccordingTo(Comparator.comparing(Interest::getName).reversed()
                            .thenComparing(Interest::getCreatedAt).reversed()
                            .thenComparing(Interest::getId).reversed());
            assertThat(page3.getContent())
                    .isSortedAccordingTo(Comparator.comparing(Interest::getName).reversed()
                            .thenComparing(Interest::getCreatedAt).reversed()
                            .thenComparing(Interest::getId).reversed());

            // 페이지들 간의 첫 요소 비교 (내림차순)
            assertThat(page1.getContent().get(0).getName())
                    .isGreaterThanOrEqualTo(page2.getContent().get(0).getName());
            assertThat(page2.getContent().get(0).getName())
                    .isGreaterThanOrEqualTo(page3.getContent().get(0).getName());

            // 각 페이지가 있는지 여부 검증
            assertThat(page1.hasNext()).isTrue();
            assertThat(page2.hasNext()).isTrue();
            assertThat(page3.hasNext()).isFalse();

            // 각 페이지 요소 수 검증
            assertThat(page1.getContent()).hasSize(pageSize);
            assertThat(page2.getContent()).hasSize(pageSize);
            assertThat(page3.getContent()).hasSize(totalCount % pageSize);
        }

        @Test
        @DisplayName("""
            정렬 기준이 구독자 수일 때,
            각 페이지의 정렬이 이름 내림차순으로 잘 되어있고,
            첫 번째 첫 요소 > 중간 페이지 첫 요소 > 마지막 페이지 첫 요소이며
            첫번째 페이지는
                content는 일부 항목
                hasNext 는 true
            중간 페이지는
                content는 일부 항목
                hasNext 는 true
            마지막 페이지는
                content는 남은 항목
                hasNext 는 false
            을 반환한다.
        """)
        void sortBySubscriberCount(){
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, totalCount);

            // [Page 1]
            // given
            InterestCursorQuery query1 = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBERCOUNT,
                    SortDirection.DESC,
                    null,
                    null,
                    null,
                    pageSize,
                    null
            );
            // when
            Slice<Interest> page1 = interestRepositoryCustom.findAllByCursor(query1);

            // [Page 2]
            // given
            InterestCursorQuery query2 = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBERCOUNT,
                    SortDirection.DESC,
                    null,
                    page1.getContent().get(page1.getContent().size() - 1).getSubscriberCount(),
                    page1.getContent().get(page1.getContent().size() - 1).getCreatedAt(),
                    pageSize,
                    null
            );

            //when
            Slice<Interest> page2 = interestRepositoryCustom.findAllByCursor(query2);

            // [Page 3]
            // given
            InterestCursorQuery query3 = new InterestCursorQuery(
                    InterestOrderBy.SUBSCRIBERCOUNT,
                    SortDirection.DESC,
                    null,
                    page2.getContent().get(page2.getContent().size() - 1).getSubscriberCount(),
                    page2.getContent().get(page2.getContent().size() - 1).getCreatedAt(),
                    pageSize,
                    null
            );
            // when
            Slice<Interest> page3 = interestRepositoryCustom.findAllByCursor(query3);

            // then
            // 각 페이지는 구독자 수 내림차순으로 정렬이 잘 되어있다.
            assertThat(page1.getContent())
                .isSortedAccordingTo(
                    Comparator.comparing(Interest::getSubscriberCount, Comparator.reverseOrder())
                            .thenComparing(Interest::getCreatedAt, Comparator.reverseOrder())
                            .thenComparing(Interest::getId, Comparator.reverseOrder())
            );
            assertThat(page2.getContent())
                .isSortedAccordingTo(
                    Comparator.comparing(Interest::getSubscriberCount, Comparator.reverseOrder())
                            .thenComparing(Interest::getCreatedAt, Comparator.reverseOrder())
                            .thenComparing(Interest::getId, Comparator.reverseOrder())
            );
            assertThat(page3.getContent())
                .isSortedAccordingTo(
                    Comparator.comparing(Interest::getSubscriberCount, Comparator.reverseOrder())
                            .thenComparing(Interest::getCreatedAt, Comparator.reverseOrder())
                            .thenComparing(Interest::getId, Comparator.reverseOrder())
            );

            // 페이지들 간의 첫 요소 비교 (내림차순)
            assertThat(page1.getContent().get(0).getSubscriberCount())
                    .isGreaterThanOrEqualTo(page2.getContent().get(0).getSubscriberCount());
            assertThat(page2.getContent().get(0).getSubscriberCount())
                    .isGreaterThanOrEqualTo(page3.getContent().get(0).getSubscriberCount());

            // 각 페이지가 있는지 여부 검증
            assertThat(page1.hasNext()).isTrue();
            assertThat(page2.hasNext()).isTrue();
            assertThat(page3.hasNext()).isFalse();

            // 각 페이지 요소 수 검증
            assertThat(page1.getContent()).hasSize(pageSize);
            assertThat(page2.getContent()).hasSize(pageSize);
            assertThat(page3.getContent()).hasSize(totalCount % pageSize);
        }
    }

    @Nested
    @DisplayName("관심사 이름이나 키워드로 검색할 수 있다")
    class searchInterest{
        @Test
        @DisplayName("키워드가 포함된 항목만 조회된다")
        void searchByKeyword() {
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, 100);

            // 예: '코딩' 키워드 포함된 항목만 조회
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.ASC,
                    null,
                    null,
                    null,
                    100,
                    "코딩"
            );

            // when
            Slice<Interest> result = interestRepositoryCustom.findAllByCursor(query);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).allSatisfy(interest ->
                    assertThat(interest.getName().contains("코딩")).isTrue()
            );
        }

        @Test
        @DisplayName("키워드가 없으면 전체 조회")
        void searchWithoutKeyword() {
            // given
            InterestTestUtils.createInterests(interestRepository, entityManager, 100);

            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.NAME,
                    SortDirection.ASC,
                    null,
                    null,
                    null,
                    100,
                    null
            );

            // when
            Slice<Interest> result = interestRepositoryCustom.findAllByCursor(query);

            // then
            assertThat(result.getContent()).hasSize(100); // 전체가 나와야 함
        }
    }
}