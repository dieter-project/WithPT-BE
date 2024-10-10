package com.sideproject.withpt.application.record.diet.repository;

import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietRepository extends JpaRepository<Diets, Long>, DietQueryRepository {

    Optional<Diets> findByMemberAndUploadDate(Member member, LocalDate uploadDate);
}
