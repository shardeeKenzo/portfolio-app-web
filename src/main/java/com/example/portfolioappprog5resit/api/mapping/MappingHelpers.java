package com.example.portfolioappprog5resit.api.mapping;

public final class MappingHelpers {
    private MappingHelpers() {}

    public static String normalizeSymbol(String s) {
        return (s == null) ? null : s.trim().toUpperCase();
    }
    public static String trimOrNull(String s) {
        return (s == null) ? null : s.trim();
    }
    public static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
