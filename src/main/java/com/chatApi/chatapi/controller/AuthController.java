package com.chatApi.chatapi.controller;

import com.chatApi.chatapi.dto.AuthResponse;
import com.chatApi.chatapi.dto.LoginDto;
import com.chatApi.chatapi.dto.RegisterDto;
import com.chatApi.chatapi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterDto dto) {
        AuthResponse res = authService.register(dto);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto dto) {
        AuthResponse res = authService.login(dto);
        return ResponseEntity.ok(res);
    }
}
