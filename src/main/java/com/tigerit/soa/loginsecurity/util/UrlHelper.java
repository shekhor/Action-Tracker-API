package com.tigerit.soa.loginsecurity.util;

/**
 *
 */
final public class UrlHelper {
    private UrlHelper() {
    }

    public static final String AUTH_LOGIN = "/api/auth/login";
    public static final String AUTH_REFRESH = "/api/auth/refresh";
    public static final String AUTH_LOGOUT = "/api/auth/logout";
    public static final String REST = "/rest";
    public static final String V2_APIDOCS = "/v2/api-docs";
    public static final String SWAGGERUI = "/swagger-ui.html";

    public static String all(String url) {
        return url + "/**";
    }

    public static String process(String url) {
        return url + "/process";
    }

    public static String prepare(String host, int port, String contextPath, String context) {
        return "http://" + host + ":" + port + "/" + contextPath + "/" + context;
    }
}