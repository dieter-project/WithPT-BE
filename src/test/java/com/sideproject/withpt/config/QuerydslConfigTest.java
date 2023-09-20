package com.sideproject.withpt.config;

import static org.assertj.core.api.Assertions.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.member.QMember;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "spring.config.location="
    + "classpath:/application-test.yml",
    classes = TestEmbeddedRedisConfig.class)
@Transactional
class QuerydslConfigTest {

    @Autowired
    EntityManager em;

    @Test
    public void querydslTest() {
        //given
        Member member = Member.builder()
            .name("test")
            .build();

        em.persist(member);

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);

        QMember qMember = QMember.member;
        Member result = jpaQueryFactory.selectFrom(qMember)
            .fetchOne();

        assertThat(result).isEqualTo(member);
        assertThat(result.getId()).isEqualTo(member.getId());
        assertThat(result.getName()).isEqualTo(member.getName());

    }
}