package com.sideproject.withpt.application.pt.service;

import static org.mockito.BDDMockito.given;

import com.sideproject.withpt.application.gym.service.GymService;
import com.sideproject.withpt.application.member.service.MemberService;
import com.sideproject.withpt.application.pt.controller.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.trainer.Trainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonalTrainingServiceTest {

    @Mock
    GymService gymService;

    @Mock
    MemberService memberService;

    @Mock
    TrainerService trainerService;

    @Mock
    PersonalTrainingRepository personalTrainingRepository;

    @InjectMocks
    PersonalTrainingService personalTrainingService;

    @Test
    public void registerPersonalTrainingMember() {
        //given
        Long gymId = 10L;
        Long memberId = 1L;
        Long trainerId = 2L;

        Member testMember = Member.builder()
            .id(memberId)
            .name("testMember")
            .build();

        Trainer testTrainer = Trainer.builder()
            .id(trainerId)
            .name("testTrainer")
            .build();

        Gym testGym = Gym.builder()
            .id(gymId)
            .name("testGym")
            .build();

        given(memberService.getMemberById(memberId))
            .willReturn(testMember);

        given(trainerService.getTrainerById(trainerId))
            .willReturn(testTrainer);

        given(gymService.getGymById(gymId))
            .willReturn(testGym);

        //when
        PersonalTrainingMemberResponse personalTrainingMemberResponse
            = personalTrainingService.registerPersonalTrainingMember(gymId, memberId, trainerId);

        //then
        Assertions.assertThat(personalTrainingMemberResponse.getMember()).isEqualTo("testMember");
        Assertions.assertThat(personalTrainingMemberResponse.getTrainer()).isEqualTo("testTrainer");
        Assertions.assertThat(personalTrainingMemberResponse.getGym()).isEqualTo("testGym");
    }
}