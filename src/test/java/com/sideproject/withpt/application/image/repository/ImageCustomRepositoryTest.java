package com.sideproject.withpt.application.image.repository;

import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.image.service.response.ImageInfoResponse;
import com.sideproject.withpt.common.type.Usages;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Image;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
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
class ImageCustomRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("이미지 리스트 조회")
    @Test
    void findAllByMemberAndUsagesAndUploadDate() {
        // given
        LocalDate uploadDate = LocalDate.of(2024, 10, 15);

        Member member = memberRepository.save(createMember("회원"));

        imageRepository.saveAll(
            List.of(
                createImage(member, Usages.EXERCISE, uploadDate, "https://withpt-s3.test1", "https://withpt-s3.test1"),
                createImage(member, Usages.EXERCISE, uploadDate, "https://withpt-s3.test2", "https://withpt-s3.test2"),
                createImage(member, Usages.EXERCISE, uploadDate, "https://withpt-s3.test3", "https://withpt-s3.test3"),
                createImage(member, Usages.EXERCISE, uploadDate, "https://withpt-s3.test4", "https://withpt-s3.test4"),
                createImage(member, Usages.BODY, uploadDate, "https://withpt-s3.test5", "https://withpt-s3.test5"),
                createImage(member, "DIET_1/DIETINFO_2", Usages.DIET, uploadDate, "https://withpt-s3.test6", "https://withpt-s3.test6")
            )
        );

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ImageInfoResponse> result = imageRepository.findAllByMemberAndUsagesAndUploadDate(member, Usages.EXERCISE, uploadDate, pageable);

        // then
        Assertions.assertThat(result.getContent()).hasSize(4)
            .extracting("usages", "uploadDate", "url")
            .containsExactlyInAnyOrder(
                tuple(Usages.EXERCISE, uploadDate, "https://withpt-s3.test1"),
                tuple(Usages.EXERCISE, uploadDate, "https://withpt-s3.test2"),
                tuple(Usages.EXERCISE, uploadDate, "https://withpt-s3.test3"),
                tuple(Usages.EXERCISE, uploadDate, "https://withpt-s3.test4")
            );
    }

    private Image createImage(Member member, Usages usages, LocalDate uploadDate, String url, String uploadUrlPath) {
        return Image.builder()
            .member(member)
            .usages(usages)
            .uploadDate(uploadDate)
            .url(url)
            .uploadUrlPath(uploadUrlPath)
            .attachType("image/png")
            .build();
    }

    private Image createImage(Member member, String usageIdentificationId, Usages usages, LocalDate uploadDate, String url, String uploadUrlPath) {
        return Image.builder()
            .member(member)
            .usageIdentificationId(usageIdentificationId)
            .usages(usages)
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