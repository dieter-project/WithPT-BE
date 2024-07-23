package com.sideproject.withpt.application.record;

import com.sideproject.withpt.domain.record.body.Body;
import com.sideproject.withpt.domain.record.diet.Diets;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AllDatesRecordResponse {

    private DietResponse diet;
    private ExerciseResponse exercise;
    private BodyInfoResponse bodyInfo;

    @Getter
    @AllArgsConstructor
    public static class DietResponse {

        private boolean isRecord;
        private double totalCalorie;
        private int targetCalorie;

        public static DietResponse convertToDietResponse(Diets diet) {
            if (diet == null) {
                return new DietResponse(false, 0, 1500);
            }
            return new DietResponse(
                true,
                diet.getTotalCalorie(),
                1500
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ExerciseResponse {

        private boolean isRecord;

        public static ExerciseResponse convertToExerciseResponse(Exercise exercise) {
            if (exercise == null) {
                return new ExerciseResponse(false);
            }
            return new ExerciseResponse(true);
        }

    }

    @Getter
    @AllArgsConstructor
    public static class BodyInfoResponse {

        private boolean isRecord;
        private double weight;
        private double targetWeight;

        public static BodyInfoResponse convertToBodyInfoResponse(Body body) {
            if (body == null) {
                return new BodyInfoResponse(false, 0, 0);
            }
            return new BodyInfoResponse(
                true,
                body.getWeight(),
                body.getTargetWeight()
            );
        }
    }


}
