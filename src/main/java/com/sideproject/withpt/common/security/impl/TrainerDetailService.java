package com.sideproject.withpt.common.security.impl;

import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.security.CustomDetailService;
import com.sideproject.withpt.common.security.CustomUserDetails;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerDetailService implements CustomDetailService {

    private final TrainerRepository trainerRepository;

    @Override
    public Role getRole() {
        return Role.TRAINER;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("접근 권한 : TRAINER");
        Trainer trainer = trainerRepository.findById(Long.valueOf(username))
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        return new CustomUserDetails(trainer.getId(), trainer.getRole());

    }
}
