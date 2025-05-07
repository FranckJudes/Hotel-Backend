package com.gallagher.hotel.services;

import com.gallagher.hotel.dto.auth.AuthResponse;
import com.gallagher.hotel.dto.auth.LoginRequest;
import com.gallagher.hotel.dto.auth.RegisterRequest;
import com.gallagher.hotel.enums.UserRole;
import com.gallagher.hotel.models.User;
import com.gallagher.hotel.repository.UserRepository;
import com.gallagher.hotel.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(UserRole.CLIENT)
                .enabled(true)
                .build();
                
        userRepository.save(user);
        
        var jwtToken = jwtTokenProvider.generateToken(user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
                
        var jwtToken = jwtTokenProvider.generateToken(user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
} 