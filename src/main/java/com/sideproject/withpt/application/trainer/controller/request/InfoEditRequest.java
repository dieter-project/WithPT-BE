package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.common.exception.validator.LocalDateType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import java.time.LocalDate;
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
public class InfoEditRequest {

    @NotBlank
    private String name;

    @LocalDateType
    private String birth;

    @ValidEnum(enumClass = Sex.class, regexp = "MAN|WOMAN")
    private Sex sex;

    public LocalDate getBirth() {
        return LocalDate.parse(birth);
    }
}
