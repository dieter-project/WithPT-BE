package com.sideproject.withpt.application.schedule.service;

import static com.sideproject.withpt.application.schedule.exception.ScheduleErrorCode.IS_EXIST_SAME_WEEKDAY;
import static com.sideproject.withpt.application.schedule.exception.ScheduleErrorCode.WORK_SCHEDULE_NOT_FOUND;

import com.sideproject.withpt.application.gym.service.GymService;
import com.sideproject.withpt.application.schedule.controller.request.WorkScheduleEditRequest;
import com.sideproject.withpt.application.schedule.controller.request.WorkScheduleEditRequest.Schedule;
import com.sideproject.withpt.application.schedule.controller.response.WorkSchedulerResponse;
import com.sideproject.withpt.application.schedule.exception.ScheduleException;
import com.sideproject.withpt.application.schedule.repository.ScheduleRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleQueryService {

    private final ScheduleRepository scheduleRepository;
    private final TrainerService trainerService;
    private final GymService gymService;

    public List<WorkSchedulerResponse> getAllWorkSchedule(Long gymId, Long trainerId) {
        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        return WorkSchedulerResponse.of(
            scheduleRepository.findAllByTrainerAndGym(trainer, gym)
        );
    }

    public WorkSchedulerResponse getWorkSchedule(Long scheduleId) {
        return WorkSchedulerResponse.of(
            scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(WORK_SCHEDULE_NOT_FOUND))
        );
    }

    @Transactional
    public void editWorkSchedule(Long trainerId, Long gymId, WorkScheduleEditRequest request) {

        // 같은 요일이 2건 이상이면 에러
        validExistSameWeekDay(request);

        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        List<WorkSchedule> originWorkSchedules = scheduleRepository.findAllByTrainerAndGym(trainer, gym);
        List<WorkScheduleEditRequest.Schedule> editWorkSchedules = request.getWorkSchedules();

        // id 가 존재하면 수정
        originWorkSchedules.forEach(originWorkSchedule -> {
            editWorkSchedules.stream()
                .filter(workSchedule -> Objects.equals(originWorkSchedule.getId(), workSchedule.getId()))
                .findFirst()
                .ifPresent(workSchedule ->
                    originWorkSchedule.editWorkScheduleTime(workSchedule.getInTime(), workSchedule.getOutTime())
                );
        });

        // id 없이 요일, 시간이 들어오면 새로 저장
        scheduleRepository.saveAll(
            editWorkSchedules.stream()
                .filter(schedule -> schedule.getId() == null)
                .map(schedule -> Schedule.toEntity(schedule, trainer, gym))
                .collect(Collectors.toList())
        );

        // 원본 list에서 없는 id 가 있으면 삭제
        scheduleRepository.deleteAllByIdInBatch(
            extractRemovedSchedules(originWorkSchedules, editWorkSchedules)
        );
    }

    private List<Long> extractRemovedSchedules(List<WorkSchedule> originWorkSchedules, List<Schedule> editWorkSchedules) {
        return originWorkSchedules.stream()
            .map(WorkSchedule::getId)
            .filter(id -> !editWorkSchedules.stream()
                .map(Schedule::getId)
                .collect(Collectors.toList()).contains(id))
            .collect(Collectors.toList());
    }

    private void validExistSameWeekDay(WorkScheduleEditRequest request) {
        request.getWorkSchedules().stream()
            .collect(Collectors.groupingBy(Schedule::getWeekday))
            .values().stream()
            .filter(list -> list.size() > 1)
            .findAny()
            .ifPresent(list -> {
                throw new ScheduleException(IS_EXIST_SAME_WEEKDAY);
            });
    }
}
