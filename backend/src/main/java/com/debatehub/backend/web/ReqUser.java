package com.debatehub.backend.web;

import com.debatehub.backend.domain.User;
import com.debatehub.backend.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

public class ReqUser {
    public static User resolve(UserRepository users, String emailFallback) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
            return users.findByEmailIgnoreCase(auth.getName()).orElse(null);
        }
        if (StringUtils.hasText(emailFallback)) {
            return users.findByEmailIgnoreCase(emailFallback).orElse(null);
        }
        return null;
    }
}
