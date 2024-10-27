package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.model.complex.GymScheduleDto;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerGymScheduleRequest {

    @NotBlank(message = "체육관 명을 입력해주세요")
    private String name;

    @NotBlank(message = "체육관 주소를 입력해주세요")
    private String address;

    private double latitude;
    private double longitude;

    @Valid
    private List<WorkScheduleRequest> workSchedules;

    public GymScheduleDto toGymScheduleSDto() {
        return GymScheduleDto.builder()
            .name(this.name)
            .address(this.address)
            .latitude(this.latitude)
            .longitude(this.longitude)
            .workSchedules(WorkScheduleRequest.toWorkScheduleDtos(workSchedules))
            .build();
    }

    public static List<GymScheduleDto> toTrainerGymScheduleDtos(List<TrainerGymScheduleRequest> gyms) {
        return gyms.stream()
            .map(TrainerGymScheduleRequest::toGymScheduleSDto)
            .collect(Collectors.toList());
    }

}
