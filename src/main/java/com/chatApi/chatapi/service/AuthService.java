package com.chatApi.chatapi.service;

import com.chatApi.chatapi.config.JwtTokenProvider;
import com.chatApi.chatapi.dto.AuthResponse;
import com.chatApi.chatapi.dto.LoginDto;
import com.chatApi.chatapi.dto.RegisterDto;
import com.chatApi.chatapi.entity.User;
import com.chatApi.chatapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse register(RegisterDto dto) {
        if (userRepository.existsByUsername(dto.username)) {
            throw new RuntimeException("Username already taken");
        }
        if (dto.email != null && userRepository.existsByEmail(dto.email)) {
            throw new RuntimeException("Email already in use");
        }
        User user = new User(dto.username, dto.email, passwordEncoder.encode(dto.password));
        userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());
        return new AuthResponse(token, user.getId(), user.getUsername());
    }

    public AuthResponse login(LoginDto dto) {
        Optional<User> userOpt = userRepository.findByUsername(dto.username);
        if (userOpt.isEmpty()) throw new RuntimeException("Invalid credentials");
        User user = userOpt.get();
        if (!passwordEncoder.matches(dto.password, user.getPassword())) throw new RuntimeException("Invalid credentials");
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());
        return new AuthResponse(token, user.getId(), user.getUsername());
    }
}
