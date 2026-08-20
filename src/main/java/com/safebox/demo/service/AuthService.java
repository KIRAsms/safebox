package com.safebox.demo.service;

import com.safebox.demo.dto.AuthResponse;
import com.safebox.demo.dto.LoginRequest;
import com.safebox.demo.dto.RegisterRequest;
import com.safebox.demo.entity.Role;
import com.safebox.demo.entity.User;
import com.safebox.demo.exception.EmailAlreadyExistsException;
import com.safebox.demo.exception.InvalidCredentialsException;
import com.safebox.demo.repository.UserRepository;
import com.safebox.demo.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return new AuthResponse(null, "User registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return new AuthResponse(jwtUtils.generateToken(request.getEmail()), "User logged in successfully");
    }
}
