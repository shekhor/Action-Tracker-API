package com.tigerit.soa.loginsecurity.auth;
/**
 *
 */
public class SecurityConstants {
    public static final String AUTH_LOGIN_URL = "/auth/login";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "typ";
    public static final String TOKEN_ISSUER = "secure-api";
    public static final String TOKEN_AUDIENCE = "secure-app";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String WHITE_LIST_ACCESS_TOKEN_PREFIX = "WL:AT:";
    public static final String WHITE_LIST_REFRESH_TOKEN_PREFIX = "WL:RT:";

    public static final String BLACK_LIST_ACCESS_TOKEN_PREFIX = "BL:AT:";
    public static final String BLACK_LIST_REFRESH_TOKEN_PREFIX = "BL:RT:";

    public static final String PORTAL_USER_TOKEN_KEY = "PORTAL:USER:TOKEN";
}

