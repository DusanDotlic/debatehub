package com.debatehub.backend.util;

import java.security.SecureRandom;

public final class Slugger {
    private static final SecureRandom RND = new SecureRandom();
    private Slugger() {}

    public static String slugify(String input) {
        String base = input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-");
        if (base.length() > 40) base = base.substring(0, 40);
        if (base.isEmpty()) base = "debate";
        return base;
    }

    public static String randomSuffix(int len) {
        final String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(alphabet.charAt(RND.nextInt(alphabet.length())));
        return sb.toString();
    }
}
