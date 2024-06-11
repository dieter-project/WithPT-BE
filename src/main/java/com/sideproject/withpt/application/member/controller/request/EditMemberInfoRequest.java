package com.sideproject.withpt.application.member.controller.request;

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
public class EditMemberInfoRequest {

    private String name;

    @ValidEnum(regexp = "MAN|WOMAN", enumClass = Sex.class)
    private Sex sex;

    @NotNull(message = "생년월일은 필수입니다.")
    @LocalDateType
    private String birth;

    private Double height;

    private Double weight;

    public LocalDate getBirth() {
        return LocalDate.parse(birth);
    }
}
