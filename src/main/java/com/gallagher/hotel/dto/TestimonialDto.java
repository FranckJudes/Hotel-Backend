package com.gallagher.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialDto {
    private Long id;
    private Long userId;
    private UserDto user;
    private String content;
    private int rating;
    private boolean approved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 