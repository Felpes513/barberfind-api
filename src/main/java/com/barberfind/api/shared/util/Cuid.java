package com.barberfind.api.shared.util;

import java.security.SecureRandom;

/**
 * Gerador simples de ID no estilo CUID/slug (não é CUID "oficial"),
 * mas atende bem para IDs únicos (varchar(40)).
 */
public final class Cuid {
    private static final SecureRandom RAND = new SecureRandom();
    private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

    private Cuid() {}

    public static String generate() {
        // 1 prefixo + 25 chars base36 => ~26-30 chars, cabe em varchar(40)
        StringBuilder sb = new StringBuilder(32);
        sb.append('c');
        for (int i = 0; i < 25; i++) {
            sb.append(ALPHABET[RAND.nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }
}