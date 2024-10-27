package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.model.single.GymDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class GymRequest {

    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public GymDto toGymDto() {
        return GymDto.builder()
            .name(this.name)
            .address(this.address)
            .latitude(this.latitude)
            .longitude(this.longitude)
            .build();
    }

    public static List<GymDto> toGymDtos(List<GymRequest> gyms) {
        return gyms.stream()
            .map(GymRequest::toGymDto)
            .collect(Collectors.toList());
    }
}
