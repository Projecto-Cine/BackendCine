package com.cine.demo.security;

public final class AuthContext {

    private static final ThreadLocal<AuthenticatedUser> CURRENT = new ThreadLocal<>();

    private AuthContext() {}

    public static void set(AuthenticatedUser user) {
        CURRENT.set(user);
    }

    public static AuthenticatedUser get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }

    public static boolean isAuthenticated() {
        return CURRENT.get() != null;
    }
}
