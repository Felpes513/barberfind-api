package com.barberfind.api.shared.util;

public final class Normalizers {
    private Normalizers() {}

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    // mantém só dígitos; ex: "(55) 119999-0000" => "551199990000"
    public static String normalizePhone(String phone) {
        return phone == null ? null : phone.replaceAll("\\D", "");
    }
}