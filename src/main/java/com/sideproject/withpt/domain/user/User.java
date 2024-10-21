package com.sideproject.withpt.domain.user;

import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
public abstract class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    private String imageUrl;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    private LocalDateTime joinDate;

    public User(String email, String password, String name, String imageUrl, LocalDate birth, Role role, Sex sex, AuthProvider authProvider, LocalDateTime joinDate) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.imageUrl = imageUrl;
        this.birth = birth;
        this.role = role;
        this.sex = sex;
        this.authProvider = authProvider;
        this.joinDate = joinDate;
    }

    public void addDefaultImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    protected void setSex(Sex sex) {
        this.sex = sex;
    }
}
