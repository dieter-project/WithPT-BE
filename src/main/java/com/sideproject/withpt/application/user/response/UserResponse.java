package com.sideproject.withpt.application.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String imageUrl;
    private LocalDate birth;
    private Sex sex;

    @Builder
    private UserResponse(Long id, String name, String email, Role role, String imageUrl, LocalDate birth, Sex sex) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.imageUrl = imageUrl;
        this.birth = birth;
        this.sex = sex;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .imageUrl(user.getImageUrl())
            .birth(user.getBirth())
            .sex(user.getSex())
            .build();
    }
}
