package com.example.server.Security;

public class EndPointsConfig {

    public static final String senderMailServer = "nguyentuongbachhy@gmail.com";

    public static final String senderPasswordServer = "zqwmerbhblgwtxrn";

    public static final String front_end_host = "http://localhost:3000";

    public static final String[] PUBLIC_GET_ENDPOINTS = {
        "/user-entity/search/existsByUsername",
        "/user-entity/search/existsByEmail",
        "/api/user-account/activate-account"
    };

    public static final String[] ADMIN_GET_ENDPOINTS = {
        "/user-entity",
        "/user-entity/**"
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = {
        "/api/user-account/register",
        "/api/user-account/login",
        "/api/user-account/forget-password",
        "/api/send-message",
        "/api/kill-app",
        "/api/start-app",
        "/api/stop-logging",
        "/api/log-key"
    };
}