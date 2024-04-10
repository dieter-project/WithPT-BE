package com.sideproject.withpt.domain.record;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.domain.member.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Long entityId;

    @Enumerated(EnumType.STRING)
    private Usages usages;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate uploadDate;

    private String url;
    private String attachType;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;

}
