package com.sideproject.withpt.application.pt.service.response;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CountOfMembersAndGymsResponse {

    private LocalDate date;
    private int totalMemberCount;
    private Slice<GymAndMemberCountResponse> gyms;

    @Builder
    private CountOfMembersAndGymsResponse(LocalDate date, int totalMemberCount, Slice<GymAndMemberCountResponse> gyms) {
        this.date = date;
        this.totalMemberCount = totalMemberCount;
        this.gyms = gyms;
    }

    public static CountOfMembersAndGymsResponse from(int totalMemberCount, LocalDate date, Slice<GymAndMemberCountResponse> gyms) {
        return CountOfMembersAndGymsResponse.builder()
            .date(date)
            .totalMemberCount(totalMemberCount)
            .gyms(gyms)
            .build();
    }
}
