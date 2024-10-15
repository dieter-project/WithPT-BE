package com.sideproject.withpt.application.record.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.image.service.response.ImageInfoResponse;
import com.sideproject.withpt.common.type.UsageType;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Image;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class ImageServiceTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ImageService imageService;

    @DisplayName("upload 날짜 이미지 조회")
    @Test
    void findAllImageByUploadDate() {
        // given
        LocalDate uploadDate = LocalDate.of(2024, 10, 15);

        Member member = memberRepository.save(createMember("회원"));

        imageRepository.saveAll(
            List.of(
                createImage(member, UsageType.EXERCISE, uploadDate, "https://withpt-s3.test1", "https://withpt-s3.test1"),
                createImage(member, UsageType.EXERCISE, uploadDate, "https://withpt-s3.test2", "https://withpt-s3.test2"),
                createImage(member, UsageType.EXERCISE, uploadDate, "https://withpt-s3.test3", "https://withpt-s3.test3"),
                createImage(member, UsageType.EXERCISE, LocalDate.of(2024, 10, 11), "https://withpt-s3.test4", "https://withpt-s3.test4"),
                createImage(member, UsageType.BODY, uploadDate, "https://withpt-s3.test5", "https://withpt-s3.test5"),
                createImage(member, "DIET_1/DIETINFO_2", UsageType.DIET, uploadDate, "https://withpt-s3.test6", "https://withpt-s3.test6")
            )
        );

        final Long memberId = member.getId();
        final Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ImageInfoResponse> responses = imageService.findAllImage(memberId, uploadDate, UsageType.EXERCISE, pageable);

        // then
        assertThat(responses.getContent()).hasSize(3)
            .extracting("usageType", "uploadDate", "url")
            .containsExactlyInAnyOrder(
                tuple(UsageType.EXERCISE, uploadDate, "https://withpt-s3.test1"),
                tuple(UsageType.EXERCISE, uploadDate, "https://withpt-s3.test2"),
                tuple(UsageType.EXERCISE, uploadDate, "https://withpt-s3.test3")
            );
    }

    @DisplayName("이미지 리스트 조회")
    @Test
    void findAllImage() {
        // given
        Member member = memberRepository.save(createMember("회원"));

        imageRepository.saveAll(
            List.of(
                createImage(member, UsageType.EXERCISE, LocalDate.of(2024, 10, 9), "https://withpt-s3.test1", "https://withpt-s3.test1"),
                createImage(member, UsageType.EXERCISE, LocalDate.of(2024, 10, 10), "https://withpt-s3.test2", "https://withpt-s3.test2"),
                createImage(member, UsageType.EXERCISE, LocalDate.of(2024, 10, 12), "https://withpt-s3.test3", "https://withpt-s3.test3"),
                createImage(member, UsageType.EXERCISE, LocalDate.of(2024, 10, 15), "https://withpt-s3.test4", "https://withpt-s3.test4"),
                createImage(member, UsageType.BODY, LocalDate.of(2024, 10, 15), "https://withpt-s3.test5", "https://withpt-s3.test5"),
                createImage(member, "DIET_1/DIETINFO_2", UsageType.DIET, LocalDate.of(2024, 10, 15), "https://withpt-s3.test6", "https://withpt-s3.test6")
            )
        );

        final Long memberId = member.getId();
        final Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ImageInfoResponse> responses = imageService.findAllImage(memberId, null, UsageType.EXERCISE, pageable);

        // then
        assertThat(responses.getContent()).hasSize(4)
            .extracting("usageType", "uploadDate", "url")
            .containsExactly(
                tuple(UsageType.EXERCISE, LocalDate.of(2024, 10, 15), "https://withpt-s3.test4"),
                tuple(UsageType.EXERCISE, LocalDate.of(2024, 10, 12), "https://withpt-s3.test3"),
                tuple(UsageType.EXERCISE, LocalDate.of(2024, 10, 10), "https://withpt-s3.test2"),
                tuple(UsageType.EXERCISE, LocalDate.of(2024, 10, 9), "https://withpt-s3.test1")
            );
    }

    private Image createImage(Member member, UsageType usageType, LocalDate uploadDate, String url, String uploadUrlPath) {
        return Image.builder()
            .member(member)
            .usageType(usageType)
            .uploadDate(uploadDate)
            .url(url)
            .uploadUrlPath(uploadUrlPath)
            .attachType("image/png")
            .build();
    }

    private Image createImage(Member member, String usageIdentificationId, UsageType usageType, LocalDate uploadDate, String url, String uploadUrlPath) {
        return Image.builder()
            .member(member)
            .usageIdentificationId(usageIdentificationId)
            .usageType(usageType)
            .uploadDate(uploadDate)
            .url(url)
            .uploadUrlPath(uploadUrlPath)
            .attachType("image/png")
            .build();
    }

    private Member createMember(String name) {
        return Member.builder()
            .email("test@test.com")
            .name(name)
            .build();
    }
}