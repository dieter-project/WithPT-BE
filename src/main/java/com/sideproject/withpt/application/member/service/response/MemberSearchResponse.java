package com.sideproject.withpt.application.member.service.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.common.type.Sex;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSearchResponse {

    private Long id;
    private String name;
    private String email;
    private String imageUrl;
    private LocalDate birth;
    private Sex sex;

    @QueryProjection
    public MemberSearchResponse(Long id, String name, String email, String imageUrl, LocalDate birth, Sex sex) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.birth = birth;
        this.sex = sex;
    }
}
