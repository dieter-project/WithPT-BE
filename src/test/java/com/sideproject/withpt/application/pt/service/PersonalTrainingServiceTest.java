package com.sideproject.withpt.application.pt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.verify;

import com.sideproject.withpt.application.gym.service.GymService;
import com.sideproject.withpt.application.member.service.MemberService;
import com.sideproject.withpt.application.pt.controller.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingQueryRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Mock
    PersonalTrainingQueryRepository trainingQueryRepository;

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
        assertThat(personalTrainingMemberResponse.getMember()).isEqualTo("testMember");
        assertThat(personalTrainingMemberResponse.getTrainer()).isEqualTo("testTrainer");
        assertThat(personalTrainingMemberResponse.getGym()).isEqualTo("testGym");
    }

    @Test
    public void deletePersonalTrainingMembers() {
        //given
        Long gymId = 10L;
        List<Long> memberIds = List.of(1L, 2L, 3L);
        Long trainerId = 2L;

        Trainer testTrainer = Trainer.builder()
            .id(trainerId)
            .name("testTrainer")
            .build();

        Gym testGym = Gym.builder()
            .id(gymId)
            .name("testGym")
            .build();

        List<Member> testMembers = new ArrayList<>();
        for (Long memberId : memberIds) {
            testMembers.add(Member.builder()
                .id(memberId)
                .name("testMember")
                .build());
        }

        given(trainerService.getTrainerById(trainerId))
            .willReturn(testTrainer);

        given(gymService.getGymById(gymId))
            .willReturn(testGym);

        given(memberService.getAllMemberById(memberIds))
            .willReturn(testMembers);

        given(trainingQueryRepository.deleteAllByMembersAndTrainerAndGym(testMembers, testTrainer, testGym))
            .willReturn((long) testMembers.size());

        //when
        long removedCount = personalTrainingService.deletePersonalTrainingMembers(gymId, trainerId, memberIds);

        //then
        assertThat(removedCount).isEqualTo(memberIds.size());
    }
}