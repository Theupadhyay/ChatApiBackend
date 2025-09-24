package com.chatApi.chatapi.dto;

public class AuthResponse {
    public String token;
    public Long userId;
    public String username;

    public AuthResponse(String token, Long userId, String username) {
        this.token = token;
        this.userId = userId;
        this.username = username;
    }
}
