package com.sideproject.withpt.application.gym.controller.response;

import com.sideproject.withpt.domain.gym.Gym;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerAllGymsResponse {

    private List<GymResponse> gyms = new ArrayList<>();

    @Getter
    @Builder
    public static class GymResponse {

        private Long id;
        private String name;
        private String address;
        private double latitude;
        private double longitude;

        public static GymResponse from(Gym gym) {
            return GymResponse.builder()
                .id(gym.getId())
                .name(gym.getName())
                .address(gym.getAddress())
                .latitude(gym.getLatitude())
                .longitude(gym.getLongitude())
                .build();
        }
    }

    public static TrainerAllGymsResponse from(List<GymResponse> gymResponses) {
        return TrainerAllGymsResponse.builder()
            .gyms(gymResponses)
            .build();
    }
}
