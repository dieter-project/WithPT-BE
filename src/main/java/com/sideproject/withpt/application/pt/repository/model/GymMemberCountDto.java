package com.sideproject.withpt.application.pt.repository.model;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class GymMemberCountDto {

    private String gymName;
    private Long memberCount;

    @QueryProjection
    public GymMemberCountDto(String gymName, Long memberCount) {
        this.gymName = gymName;
        this.memberCount = memberCount;
    }
}
