package com.sideproject.withpt.application.member.controller.request;

import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.common.exception.validator.LocalDateType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
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
public class EditMemberDietTypeRequest {

    @ValidEnum(enumClass = DietType.class)
    private DietType dietType;
}
