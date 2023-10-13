package com.sideproject.withpt.application.pt.controller.response;

import com.sideproject.withpt.application.gym.controller.response.TrainerAllGymsResponse;
import com.sideproject.withpt.application.gym.controller.response.TrainerAllGymsResponse.GymResponse;
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
public class GymsAndNumberOfMembersResponse {

    @Builder.Default
    private Long totalMembers = 0L;
    private List<GymResponse> gyms = new ArrayList<>();

    @Getter
    @Builder
    public static class GymResponse {

        private Long id;
        private String name;
        private String address;
        private double latitude;
        private double longitude;
        private Long memberCount;

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

    public static GymsAndNumberOfMembersResponse from(List<GymResponse> gymResponses, Long totalMembers) {
        return GymsAndNumberOfMembersResponse.builder()
            .totalMembers(totalMembers)
            .gyms(gymResponses)
            .build();
    }
}
