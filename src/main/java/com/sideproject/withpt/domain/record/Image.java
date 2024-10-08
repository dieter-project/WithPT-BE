package com.sideproject.withpt.domain.record;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.common.type.Usages;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.member.Member;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String usageIdentificationId;

    @Enumerated(EnumType.STRING)
    private Usages usages;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate uploadDate;

    private String url;
    private String uploadUrlPath;

    private String attachType;

//    @CreatedDate
//    @Column(updatable = false, nullable = false)
//    private LocalDateTime createdDate;

}
