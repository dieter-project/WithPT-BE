package com.sideproject.withpt.application.pt.controller.response;

import com.sideproject.withpt.domain.gym.Gym;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GymResponse {

    private Long id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private Long memberCount;

    @Builder
    private GymResponse(Long id, String name, String address, double latitude, double longitude, Long memberCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.memberCount = memberCount;
    }

    public static GymResponse from(Gym gym, Long memberCount) {
        return GymResponse.builder()
            .id(gym.getId())
            .name(gym.getName())
            .address(gym.getAddress())
            .latitude(gym.getLatitude())
            .longitude(gym.getLongitude())
            .memberCount(memberCount)
            .build();
    }
}
