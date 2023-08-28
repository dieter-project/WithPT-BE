package com.sideproject.withpt.application.member.repository;

import com.sideproject.withpt.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findById(Long id);

}
