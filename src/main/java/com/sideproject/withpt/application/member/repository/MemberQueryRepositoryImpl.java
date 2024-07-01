package com.sideproject.withpt.application.member.repository;

import static com.sideproject.withpt.domain.member.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.member.controller.response.MemberSearchResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<MemberSearchResponse> findBySearchOption(Pageable pageable, String name, String nickname) {
        List<MemberSearchResponse> members = jpaQueryFactory
            .select(Projections.bean(
                MemberSearchResponse.class,
                member.id,
                member.name,
                member.imageUrl,
                member.authentication.birth,
                member.authentication.sex
            ))
            .from(member)
            .where(eqName(name), eqNickName(nickname))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(member.count())
            .from(member)
            .where(eqName(name), eqNickName(nickname));

        return PageableExecutionUtils.getPage(members, pageable, countQuery::fetchOne);
    }

    private BooleanExpression eqName(String name) {
        return StringUtils.hasText(name) ? member.name.eq(name) : null;
    }

    private BooleanExpression eqNickName(String nickname) {
        return StringUtils.hasText(nickname) ? member.name.eq(nickname) : null;
    }


}
