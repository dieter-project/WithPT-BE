package com.sideproject.withpt.application.pt.service.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalTrainingMemberResponse {

    private String member;
    private String trainer;
    private String gym;

    public static PersonalTrainingMemberResponse from(String member, String trainer, String gym) {
        return PersonalTrainingMemberResponse.builder()
            .member(member)
            .trainer(trainer)
            .gym(gym)
            .build();
    }
}
