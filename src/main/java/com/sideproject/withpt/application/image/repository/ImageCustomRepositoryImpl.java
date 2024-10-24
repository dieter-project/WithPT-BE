package com.sideproject.withpt.application.image.repository;

import static com.sideproject.withpt.domain.record.QImage.image;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.record.image.service.response.ImageInfoResponse;
import com.sideproject.withpt.application.record.image.service.response.QImageInfoResponse;
import com.sideproject.withpt.common.type.UsageType;
import com.sideproject.withpt.domain.user.member.Member;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
@RequiredArgsConstructor
public class ImageCustomRepositoryImpl implements ImageCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<ImageInfoResponse> findAllByMemberAndUsagesAndUploadDate(Member member, UsageType usageType, LocalDate uploadDate, Pageable pageable) {
        List<ImageInfoResponse> contents = jpaQueryFactory
            .select(
                new QImageInfoResponse(
                    image.id,
                    image.usageType,
                    image.uploadDate,
                    image.url,
                    image.attachType
                )
            )
            .from(image)
            .where(image.member.eq(member)
                .and(image.usageType.eq(usageType))
                .and(uploadDateEq(uploadDate))
            )
            .orderBy(image.uploadDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    private BooleanExpression uploadDateEq(LocalDate uploadDate) {
        return ObjectUtils.isEmpty(uploadDate) ? null : image.uploadDate.eq(uploadDate);
    }
}
