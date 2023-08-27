package com.sideproject.withpt.common.security;

import com.sideproject.withpt.application.type.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomDetailService extends UserDetailsService {
    Role getRole();
}
