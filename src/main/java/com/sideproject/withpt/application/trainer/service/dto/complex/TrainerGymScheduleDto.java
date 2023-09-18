package com.sideproject.withpt.application.trainer.service.dto.complex;

import com.sideproject.withpt.application.trainer.service.dto.single.GymDto;
import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.domain.gym.Gym;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainerGymScheduleDto {

    private String name;
    private String address;
    private double latitude;
    private double longitude;

    @Builder.Default
    private List<WorkScheduleDto> workSchedules = new ArrayList<>();

    public Gym toGymEntity() {
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
