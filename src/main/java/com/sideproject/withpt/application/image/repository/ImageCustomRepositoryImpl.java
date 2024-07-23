package com.sideproject.withpt.application.image.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.record.body.controller.response.BodyImageInfoResponse;
import com.sideproject.withpt.application.record.body.controller.response.BodyImageResponse;
import com.sideproject.withpt.application.record.body.controller.response.QBodyImageInfoResponse;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import org.springframework.util.ObjectUtils;

import static com.sideproject.withpt.domain.record.QImage.image;

@Repository
@RequiredArgsConstructor
public class ImageCustomRepositoryImpl implements ImageCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<BodyImageResponse> findAllBodyImage(Pageable pageable, Long memberId, Usages usages) {
        List<Tuple> bodyImageResponseList = jpaQueryFactory
                .select(image.uploadDate, image.url)
                .from(image)
                .where(image.member.id.eq(memberId))
                .orderBy(image.uploadDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        Map<LocalDate, List<String>> bodyImageMap = new HashMap<>();

        for (Tuple tuple : bodyImageResponseList) {
            LocalDate uploadDate = tuple.get(image.uploadDate);
            String url = tuple.get(image.url);

            bodyImageMap.computeIfAbsent(uploadDate, k -> new ArrayList<>()).add(url);
        }

        List<BodyImageResponse> content = new ArrayList<>();

        for (Map.Entry<LocalDate, List<String>> entry : bodyImageMap.entrySet()) {
            content.add(new BodyImageResponse(entry.getKey(), entry.getValue()));
        }

        boolean hasText = content.size() > pageable.getPageSize();
        if (hasText) {
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasText);
    }

    @Override
    public Slice<BodyImageInfoResponse> findAllByMemberAndUsagesAndUploadDate(Pageable pageable, Member member, Usages usages, LocalDate uploadDate) {
        List<BodyImageInfoResponse> bodyImageInfoResponseList = jpaQueryFactory
            .select(
                new QBodyImageInfoResponse(
                    image.id,
                    image.usages,
                    image.uploadDate,
                    image.url,
                    image.attachType
                )
            )
            .from(image)
            .where(image.member.eq(member)
                .and(image.usages.eq(usages))
                .and(uploadDateEq(uploadDate))
            )
            .orderBy(image.uploadDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<BodyImageInfoResponse> content = new ArrayList<>(bodyImageInfoResponseList);

        boolean hasNext = false;

        if(content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression uploadDateEq(LocalDate uploadDate) {
        return ObjectUtils.isEmpty(uploadDate) ? null : image.uploadDate.eq(uploadDate);
    }
}
