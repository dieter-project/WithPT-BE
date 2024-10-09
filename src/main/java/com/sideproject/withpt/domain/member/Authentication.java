package com.sideproject.withpt.domain.member;

import com.sideproject.withpt.common.type.LoginType;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_authentication")
public class Authentication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authentication_id")
    private Long id;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private LocalDateTime joinDate;

    public void editMemberInfo(LocalDate birth, Sex sex) {
        this.birth = birth;
        this.sex = sex;
    }
}
