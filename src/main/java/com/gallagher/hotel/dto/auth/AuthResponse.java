package com.gallagher.hotel.dto.auth;

import com.gallagher.hotel.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private Long userId;
    private String username;
    private UserRole role;
} 