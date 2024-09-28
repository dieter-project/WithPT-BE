package com.sideproject.withpt.application.trainer.service.dto.complex;

import com.sideproject.withpt.application.trainer.service.dto.single.GymDto;
import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.domain.gym.Gym;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GymScheduleDto {

    private final String name;
    private final String address;
    private final double latitude;
    private final double longitude;
    private final List<WorkScheduleDto> workSchedules;

    @Builder
    private GymScheduleDto(String name, String address, double latitude, double longitude, List<WorkScheduleDto> workSchedules) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.workSchedules = workSchedules;
    }

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
