package com.sideproject.withpt.application.gym.service.response;

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

    @Builder
    private GymResponse(Long id, String name, String address, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static GymResponse of(Gym gym) {
        return GymResponse.builder()
            .id(gym.getId())
            .name(gym.getName())
            .address(gym.getAddress())
            .latitude(gym.getLatitude())
            .longitude(gym.getLongitude())
            .build();
    }
}
