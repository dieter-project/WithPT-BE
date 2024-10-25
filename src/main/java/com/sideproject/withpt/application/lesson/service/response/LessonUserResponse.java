package com.sideproject.withpt.application.lesson.service.response;

import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LessonUserResponse {

    private Long id;
    private String name;
    private Role role;

    @Builder
    private LessonUserResponse(Long id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public static LessonUserResponse of(User user) {
        return LessonUserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .role(user.getRole())
            .build();
    }
}
