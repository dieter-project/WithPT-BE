package com.sideproject.withpt.application.image.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.body.controller.response.BodyImageResponse;
import com.sideproject.withpt.application.type.Usages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

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

}
