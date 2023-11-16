package com.sideproject.withpt.application.body.service;

import com.sideproject.withpt.application.body.dto.request.BodyInfoRequest;
import com.sideproject.withpt.application.body.dto.request.WeightInfoRequest;
import com.sideproject.withpt.application.body.dto.response.WeightInfoResponse;
import com.sideproject.withpt.application.body.exception.BodyException;
import com.sideproject.withpt.application.body.repository.BodyRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Body;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BodyServiceTest {

    @Mock
    private BodyRepository bodyRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BodyService bodyService;

    @Test
    @DisplayName("체중 및 전체 신체 정보 조회하기")
    void findWeightAndBodyInfo() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(bodyRepository.findRecentBodyInfo(anyLong(), any()))
                .willReturn(Optional.of(createAddWeightRequest().toEntity(createMember())));

        // when
        WeightInfoResponse weightInfo = bodyService.findWeightInfo(1L, LocalDate.now());

        // then
        then(bodyRepository).should(times(1)).findRecentBodyInfo(anyLong(), any());
        assertThat(weightInfo.getBmi()).isEqualTo(32.1);
        assertThat(weightInfo.getWeight()).isEqualTo(55.5);
    }

    @Test
    @DisplayName("선택한 날짜보다 이전 데이터 중에서 최근 데이터를 조회한다 - 이전 데이터가 없는 경우")
    void findWeightAndBodyInfoBeforeDate() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(bodyRepository.findRecentBodyInfo(anyLong(), any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> {
                    bodyService.findWeightInfo(1L, LocalDate.of(2013, 12, 12));
                })
                .isExactlyInstanceOf(BodyException.class)
                .hasMessage(BodyException.BODY_NOT_EXIST.getMessage());
    }

    @Test
    @DisplayName("체중 저장할 때 오늘 날짜 기록이 없으면 새로운 기록을 생성한다")
    void saveWeight() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(bodyRepository.findTodayBodyInfo(anyLong(), any())).willReturn(Optional.empty());
        given(bodyRepository.findRecentBodyInfo(anyLong(), any()))
                .willReturn(Optional.of(createAddWeightRequest().toEntity(createMember())));

        // when
        bodyService.saveWeight(1L, createAddWeight());

        // then
        then(bodyRepository).should(times(1)).save(any(Body.class));
    }

    @Test
    @DisplayName("체중 저장할 때 오늘 날짜 기록이 있으면 수정한다")
    void updateWeight() {
        // given
        WeightInfoRequest weightRequest =
                WeightInfoRequest.builder()
                .weight(55.0)
                .bodyRecordDate(LocalDate.now())
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(bodyRepository.findTodayBodyInfo(anyLong(), any()))
                .willReturn(Optional.of(createAddWeightRequest().toEntity(createMember())));

        // when
        bodyService.saveWeight(1L, weightRequest);
        Body body = bodyRepository.findTodayBodyInfo(1L, LocalDate.now()).get();

        // then
        assertThat(body.getWeight()).isEqualTo(55.0);
    }

    @Test
    @DisplayName("체중 저장할 때 Member 테이블의 체중(weight) 컬럼도 함께 수정한다")
    void updateMemberWeight() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(bodyRepository.findTodayBodyInfo(anyLong(), any())).willReturn(Optional.empty());
        given(bodyRepository.findRecentBodyInfo(anyLong(), any()))
                .willReturn(Optional.of(createAddWeightRequest().toEntity(createMember())));

        // when
        bodyService.saveWeight(1L, createAddWeight());
        Member member = memberRepository.findById(1L).get();

        // then
        then(bodyRepository).should(times(1)).save(any(Body.class));
        assertThat(member.getWeight()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("신체 정보 저장할 때 오늘 날짜 기록이 없으면 새로운 기록을 생성한다")
    void saveBodyInfo() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(bodyRepository.findTodayBodyInfo(anyLong(), any())).willReturn(Optional.empty());
        given(bodyRepository.findRecentBodyInfo(anyLong(), any()))
                .willReturn(Optional.of(createAddWeightRequest().toEntity(createMember())));

        // when
        bodyService.saveBodyInfo(1L, createAddWeightRequest());

        // then
        then(bodyRepository).should(times(1)).save(any(Body.class));
    }

    @Test
    @DisplayName("신체 정보 저장할 때 오늘 날짜 기록이 있으면 수정한다")
    void updateBodyInfo() {
        // given
        BodyInfoRequest bodyInfoRequest =
                BodyInfoRequest.builder()
                .bodyRecordDate(LocalDate.now())
                .bmi(13.3)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(bodyRepository.findTodayBodyInfo(anyLong(), any()))
                .willReturn(Optional.of(createAddWeightRequest().toEntity(createMember())));

        // when
        bodyService.saveBodyInfo(1L, bodyInfoRequest);
        Body body = bodyRepository.findTodayBodyInfo(1L, LocalDate.now()).get();

        // then
        assertThat(body.getBmi()).isEqualTo(13.3);
    }

    private BodyInfoRequest createAddWeightRequest() {
        return BodyInfoRequest.builder()
                .skeletalMuscle(23.5)
                .bodyFatPercentage(15.2)
                .bmi(32.1)
                .bodyRecordDate(LocalDate.of(2023, 9, 27))
                .build();
    }

    private WeightInfoRequest createAddWeight() {
        return WeightInfoRequest.builder()
                .weight(10.0)
                .bodyRecordDate(LocalDate.of(2023, 9, 27))
                .build();
    }

    private Member createMember() {
        return Member.builder()
                .id(1L)
                .weight(55.5)
                .nickname("test")
                .build();
    }

}
