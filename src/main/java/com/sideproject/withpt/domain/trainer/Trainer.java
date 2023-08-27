package com.sideproject.withpt.domain.trainer;

import com.sideproject.withpt.application.type.LoginType;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.domain.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trainer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    private String name;

    private String imageUrl;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Career> careers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthProvider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime joinDate;

    public void addCareer(Career career) {
        careers.add(career);
        career.addTrainer(this);
    }


}
