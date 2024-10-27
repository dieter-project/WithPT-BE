package com.sideproject.withpt.application.trainer.service.model.single;

import com.sideproject.withpt.domain.gym.Gym;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GymDto {

    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public Gym toEntity() {
        return Gym.builder()
            .name(this.name)
            .address(this.address)
            .latitude(this.latitude)
            .longitude(this.longitude)
            .build();
    }

    public List<Gym> toGymEntities(List<GymDto> gymDtos) {
        return gymDtos.stream()
            .map(GymDto::toEntity)
            .collect(Collectors.toList());
    }
}
