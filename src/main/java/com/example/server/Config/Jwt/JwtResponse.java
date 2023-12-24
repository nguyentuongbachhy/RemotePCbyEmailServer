package com.example.server.Config.Jwt;

public class JwtResponse {
    private final String jwt;

    public String getJwt() {
        return jwt;
    }

    public JwtResponse(String jwt) {
        this.jwt = jwt;
    }
}
