package com.sideproject.withpt.application.member.controller.response;

import com.sideproject.withpt.application.type.Sex;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberSearchResponse {

    private Long id;
    private String name;
    private String nickname;
    private String imageUrl;
    private LocalDate birth;
    private Sex sex;

}
