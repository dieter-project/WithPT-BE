package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.complex.TrainerGymScheduleDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TrainerGymScheduleRequest {

    private String name;
    private String address;
    private double latitude;
    private double longitude;

    @Builder.Default
    private List<WorkScheduleRequest> workSchedules = new ArrayList<>();

    public TrainerGymScheduleDto toGymScheduleSDto() {
        return TrainerGymScheduleDto.builder()
            .name(this.name)
            .address(this.address)
            .latitude(this.latitude)
            .longitude(this.longitude)
            .workSchedules(WorkScheduleRequest.toWorkScheduleDtos(workSchedules))
            .build();
    }

    public static List<TrainerGymScheduleDto> toTrainerGymScheduleDtos(List<TrainerGymScheduleRequest> gyms) {
        return gyms.stream()
            .map(TrainerGymScheduleRequest::toGymScheduleSDto)
            .collect(Collectors.toList());
    }

}
