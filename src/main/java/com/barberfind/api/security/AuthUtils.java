package com.barberfind.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    private AuthUtils() {}

    public static String getUserId() {
        return getAuth().getName();
    }

    public static String getRole() {
        return getAuth().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.replace("ROLE_", ""))
                .findFirst()
                .orElse("");
    }

    private static Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}