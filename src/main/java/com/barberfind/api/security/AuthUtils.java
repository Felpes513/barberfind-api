package com.barberfind.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    private AuthUtils() {}

    public static String getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Normalmente auth.getName() vira o "sub" do JWT.
        return auth.getName();
    }
}