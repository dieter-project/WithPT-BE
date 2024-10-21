package com.sideproject.withpt.application.member.repository;

import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.domain.user.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

    Optional<Member> findByEmailAndAuthProvider(String email, AuthProvider authProvider);

    boolean existsByEmail(String email);

    Optional<Member> findById(Long id);
}
