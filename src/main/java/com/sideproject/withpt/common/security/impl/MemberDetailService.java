package com.sideproject.withpt.common.security.impl;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.security.CustomDetailService;
import com.sideproject.withpt.common.security.CustomUserDetails;
import com.sideproject.withpt.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberDetailService implements CustomDetailService {

    private final MemberRepository memberRepository;

    @Override
    public Role getRole() {
        return Role.MEMBER;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("접근 권한 : MEMBER");
        Member member = memberRepository.findById(Long.valueOf(username))
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        return new CustomUserDetails(member.getId(), member.getRole());

    }
}
