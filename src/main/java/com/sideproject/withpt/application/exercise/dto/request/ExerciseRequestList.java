package com.sideproject.withpt.application.exercise.dto.request;

import com.sideproject.withpt.application.exercise.exception.validator.ValidExerciseType;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Bookmark;
import com.sideproject.withpt.domain.record.Exercise;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseRequestList {

   @NotNull @Valid
   private List<ExerciseRequest> exerciseRequest;

   @Getter
   @Builder
   @AllArgsConstructor
   @NoArgsConstructor(access = AccessLevel.PROTECTED)
   @ValidExerciseType
   public static class ExerciseRequest {
      @NotNull(message = "운동 일자를 입력해주세요.")
      private LocalDateTime exerciseDate;

      @NotBlank(message = "운동명을 입력해주세요.")
      private String title;

      private int weight;
      private int set;
      private int times;
      private int hour;

      @NotBlank(message = "북마크 여부를 입력해주세요.")
      private String bookmarkYn;

      @ValidEnum(enumClass = BodyPart.class)
      private BodyPart bodyPart;

      @ValidEnum(enumClass = ExerciseType.class)
      private ExerciseType exerciseType;

      private List<MultipartFile> file;

      public Exercise toExerciseEntity(Member member) {
         return Exercise.builder()
                 .member(member)
                 .title(title)
                 .weight(weight)
                 .set(set)
                 .times(times)
                 .hour(hour)
                 .bodyPart(bodyPart)
                 .exerciseType(exerciseType)
                 .exerciseDate(exerciseDate)
                 .build();
      }

      public Bookmark toBookmarkEntity(Member member) {
         return Bookmark.builder()
                 .member(member)
                 .title(title)
                 .weight(weight)
                 .set(set)
                 .times(times)
                 .hour(hour)
                 .bodyPart(bodyPart)
                 .exerciseType(exerciseType)
                 .build();
      }
   }

}
