package com.sideproject.withpt.application.exercise.Fixture;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseRequest;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.record.Exercise;

import java.time.LocalDateTime;
import java.util.List;

public class ExerciseFixture {

    public static final Exercise EXERCISE = createExercise();
    public static final List<Exercise> EXERCISE_LIST = createExerciseList();
    public static final ExerciseRequest EXERCISE_REQUEST = createExerciseRequest();
    public static final List<ExerciseRequest> EXERCISE_REQUEST_LIST = createExerciseRequestList();

    private static Exercise createExercise() {
        return Exercise.builder()
                .id(1L)
                .title("운동명")
                .weight(300)
                .set(3)
                .bookmarkYn("N")
                .bodyPart(BodyPart.LOWER_BODY)
                .exerciseType(ExerciseType.ANAEROBIC)
                .exerciseDate(LocalDateTime.now())
                .member(MemberFixture.MEMBER)
                .build();
    }

    private static List<Exercise> createExerciseList() {
        Exercise exercise1 =Exercise.builder()
                .id(1L)
                .title("운동명1")
                .weight(300)
                .set(3)
                .bookmarkYn("N")
                .bodyPart(BodyPart.LOWER_BODY)
                .exerciseType(ExerciseType.ANAEROBIC)
                .exerciseDate(LocalDateTime.now())
                .member(MemberFixture.MEMBER)
                .build();

        Exercise exercise2 =Exercise.builder()
                .id(2L)
                .title("운동명2")
                .weight(300)
                .set(3)
                .bookmarkYn("N")
                .bodyPart(BodyPart.LOWER_BODY)
                .exerciseType(ExerciseType.ANAEROBIC)
                .exerciseDate(LocalDateTime.now())
                .member(MemberFixture.MEMBER)
                .build();

        List<Exercise> requests = List.of(exercise1, exercise2);
        return requests;
    }

    private static ExerciseRequest createExerciseRequest() {
        return ExerciseRequest.builder()
                .title("운동명")
                .weight(300)
                .set(3)
                .bookmarkYn("N")
                .bodyPart(BodyPart.LOWER_BODY)
                .exerciseType(ExerciseType.ANAEROBIC)
                .exerciseDate(LocalDateTime.now())
                .build();
    }

    private static List<ExerciseRequest> createExerciseRequestList() {
        ExerciseRequest exercise1 = ExerciseRequest.builder()
                .title("운동명1")
                .weight(300)
                .set(3)
                .bookmarkYn("N")
                .bodyPart(BodyPart.LOWER_BODY)
                .exerciseType(ExerciseType.ANAEROBIC)
                .exerciseDate(LocalDateTime.now())
                .build();

        ExerciseRequest exercise2 = ExerciseRequest.builder()
                .title("운동명2")
                .weight(300)
                .set(3)
                .bookmarkYn("N")
                .bodyPart(BodyPart.LOWER_BODY)
                .exerciseType(ExerciseType.ANAEROBIC)
                .exerciseDate(LocalDateTime.now())
                .build();

        List<ExerciseRequest> requests = List.of(exercise1, exercise2);
        return requests;
    }

}
