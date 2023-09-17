package com.sideproject.withpt.application.exercise.Fixture;

import com.sideproject.withpt.domain.member.Member;

public class MemberFixture {

    public static final Member MEMBER = createMember();

    private static Member createMember() {
        return Member.builder()
                .id(1L)
                .nickname("test")
                .build();
    }

}
