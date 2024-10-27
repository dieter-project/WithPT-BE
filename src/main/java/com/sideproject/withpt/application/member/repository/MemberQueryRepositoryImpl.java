package com.sideproject.withpt.application.member.repository;

import static com.sideproject.withpt.domain.user.member.QMember.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.member.service.response.MemberSearchResponse;
import com.sideproject.withpt.application.member.controller.response.QMemberSearchResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<MemberSearchResponse> findBySearchOption(Pageable pageable, String name) {

        List<MemberSearchResponse> contents = jpaQueryFactory
            .select(
                new QMemberSearchResponse(
                    member.id,
                    member.name,
                    member.email,
                    member.imageUrl,
                    member.birth,
                    member.sex
                )
            )
            .from(member)
            .where(eqName(name))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(
                member.name.asc(),
                member.birth.asc()
            )
            .fetch();

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    private BooleanExpression eqName(String name) {
        return StringUtils.hasText(name) ? member.name.contains(name) : null;
    }

}
