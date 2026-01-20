package com.codeit.monew.domain.interest.unit.service;

import com.codeit.monew.domain.interest.dto.query.InterestCursorQuery;
import com.codeit.monew.domain.interest.dto.request.InterestCreatedRequest;
import com.codeit.monew.domain.interest.dto.request.InterestCursorPageRequest;
import com.codeit.monew.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.monew.domain.interest.dto.response.InterestCommonResponse;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.web.InterestNotFoundException;
import com.codeit.monew.domain.interest.mapper.InterestMapper;
import com.codeit.monew.domain.interest.mapper.InterestQueryMapper;
import com.codeit.monew.domain.interest.policy.InterestNamePolicy;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.interest.repository.InterestRepositoryCustomImpl;
import com.codeit.monew.domain.interest.service.InterestServiceImpl;
import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.SortDirection;
import com.codeit.monew.domain.interestkeyword.entity.InterestKeyword;
import com.codeit.monew.domain.interestuser.repository.InterestUserRepository;
import com.codeit.monew.global.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class InterestServiceImplTest {
    @Mock
    InterestRepository interestRepository;

    @Mock
    InterestNamePolicy interestNamePolicy;

    @Mock
    InterestRepositoryCustomImpl interestRepositoryCustom;

    @Mock
    InterestUserRepository interestUserRepository;

    @Mock
    InterestQueryMapper interestQueryMapper;

    @Mock
    InterestMapper interestMapper;

    @InjectMocks
    InterestServiceImpl interestService;

    @Nested
    @DisplayName("관심사 생성 - 상태 검증")
    class CreateInterestState {

        @Test
        @DisplayName("성공: 유효한 이름과 키워드가 주어지면 관심사가 생성된다")
        void success_create_interest() {
            //given
            String name = "백엔드";
            List<String> keywords = Arrays.asList("java", "spring");
            InterestCreatedRequest request = new InterestCreatedRequest(name, keywords);
            InterestCommonResponse response = new InterestCommonResponse(
                    UUID.randomUUID(), name, keywords, 0, false);
            given(interestRepository.save(any(Interest.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(interestMapper.toDto(any(Interest.class), eq(false))).willReturn(response);

            // when
            InterestCommonResponse result = interestService.create(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(name);
            assertThat(result.keywords())
                    .containsExactlyElementsOf(keywords);
        }
    }

    @Nested
    @DisplayName("관심사 생성 - 행위 검증")
    class CreateInterestBehavior {
        @Test
        @DisplayName("성공: 관심사 생성 시 저장소의 save가 호출된다")
        void success_create_interest_save(){
            //given
            String name = "백엔드";
            List<String> keywords = Arrays.asList("java", "spring");
            InterestCreatedRequest request = new InterestCreatedRequest(name, keywords);
            InterestCommonResponse response = new InterestCommonResponse(
                    UUID.randomUUID(), name, keywords, 0, false);
            given(interestRepository.findAll()).willReturn(List.of());
            given(interestRepository.save(any(Interest.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(interestMapper.toDto(any(Interest.class), eq(false))).willReturn(response);

            //when
            interestService.create(request);

            //then
            then(interestRepository).should().save(any(Interest.class));
        }
    }

    @Nested
    @DisplayName("관심사 수정 - 상태 검증")
    class UpdateInterestState {
        @Test
        @DisplayName("성공: 관심사 수정 시 키워드가 변경된다")
        void success_update_interest_keywords(){
            // given
            UUID interestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            String name = "백엔드";
            List<String> oldKeywords = List.of("java", "spring");
            List<String> newKeywords = List.of("DB", "Spring boot");

            InterestUpdateRequest request = new InterestUpdateRequest(newKeywords);
            Interest interest = new Interest(name, oldKeywords);
            InterestCommonResponse response = new InterestCommonResponse(
                    UUID.randomUUID(), name, newKeywords, 0, false);

            given(interestRepository.findById(interestId))
                    .willReturn(Optional.of(interest));
            given(interestUserRepository.existsByUserIdAndInterestId(any(),any())).willReturn(false);
            given(interestMapper.toDto(any(Interest.class), eq(false))).willReturn(response);

            // when
            interestService.editKeywords(userId, interestId, request);

            // then
            assertThat(interest.getKeywords())
                    .extracting(InterestKeyword::getKeyword)
                    .containsExactly("DB", "Spring boot");
        }

        @Test
        @DisplayName("실패: 관심사를 id로 찾을 수 없을 때 예외가 발생한다")
        void fail_update_interest_not_found(){
            // given
            UUID interestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            String name = "백엔드";
            List<String> oldKeywords = List.of("java", "spring");
            List<String> newKeywords = List.of("DB", "Spring boot");
            InterestUpdateRequest request = new InterestUpdateRequest(newKeywords);

            given(interestRepository.findById(interestId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> interestService.editKeywords(userId, interestId, request))
                    .isInstanceOf(InterestNotFoundException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("관심사 삭제 - 행위 검증")
    class DeleteInterestState {
        @Test
        @DisplayName("성공: 관심사 삭제 시 저장소의 delete가 호출된다")
        void success_delete_interest(){
            // given
            UUID interestId = UUID.randomUUID();
            Interest interest = new Interest("백엔드", List.of("java", "spring"));
            given(interestRepository.findById(interestId))
                    .willReturn(Optional.of(interest));

            // when
            interestService.delete(interestId);

            // then
            then(interestRepository).should().deleteById(interestId);
        }

        @Test
        @DisplayName("실패: 저장소에 존재하지 않는 관심사 UUID를 줄 경우 예외가 발생한다")
        void fail_delete_interest_not_found(){
            // given
            UUID interestId = UUID.randomUUID();
            given(interestRepository.findById(interestId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> interestService.delete(interestId))
                    .isInstanceOf(InterestNotFoundException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("관심사 조회")
    class FindInterest{
        @Test
        @DisplayName("관심사 조회를 하면 repository의 조회가 호출된다")
        void find_interest_(){
            // given
            UUID userId = UUID.randomUUID();
            UUID interestId = UUID.randomUUID();

            String name = "관심사이름";
            List<String> keywords = List.of("java", "spring");
            Interest interest = new Interest(name, keywords);
            InterestCommonResponse response = new InterestCommonResponse(
                    interestId, name, keywords, 0, false
            );
            Slice<Interest> interestSlice = new SliceImpl<>(List.of(interest));
            InterestCursorPageRequest request = new InterestCursorPageRequest(
                    "name", "desc",null,null,10,null);
            InterestCursorQuery query = new InterestCursorQuery(
                    InterestOrderBy.NAME, SortDirection.DESC,null,null,null,10,null);

            given(interestQueryMapper.toQuery(request)).willReturn(query);
            given(interestUserRepository.existsByUserIdAndInterestId(any(),any())).willReturn(false);
            given(interestRepositoryCustom.findAllByCursor(any())).willReturn(interestSlice);
            given(interestMapper.toDto(interest, false)).willReturn(response);

            // when
            interestService.getInterests(userId,request);

            // then
            then(interestRepositoryCustom).should(times(1)).findAllByCursor(any());
        }
    }
}