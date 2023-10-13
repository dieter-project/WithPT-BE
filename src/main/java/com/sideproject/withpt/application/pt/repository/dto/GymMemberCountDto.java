package com.sideproject.withpt.application.pt.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
