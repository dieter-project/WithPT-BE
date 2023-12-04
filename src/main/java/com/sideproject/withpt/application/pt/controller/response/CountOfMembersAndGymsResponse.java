package com.sideproject.withpt.application.pt.controller.response;

import com.sideproject.withpt.domain.gym.Gym;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CountOfMembersAndGymsResponse {

    private Long id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private Long memberCount;

    public static CountOfMembersAndGymsResponse from(Gym gym, Long memberCount) {
        return CountOfMembersAndGymsResponse.builder()
            .id(gym.getId())
            .name(gym.getName())
            .address(gym.getAddress())
            .latitude(gym.getLatitude())
            .longitude(gym.getLongitude())
            .memberCount(memberCount)
            .build();
    }
}
