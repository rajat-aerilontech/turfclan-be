package com.aerilon.turfclan.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    public static void createHttpOnlyCookie(HttpServletResponse response, String name, String value, int maxAgeSecs) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeSecs);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    public static void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
