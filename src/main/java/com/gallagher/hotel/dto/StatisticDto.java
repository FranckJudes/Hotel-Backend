package com.gallagher.hotel.dto;

import com.gallagher.hotel.enums.StatisticType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticDto {
    private Long id;
    private StatisticType type;
    private LocalDate date;
    private BigDecimal value;
    private String valueString;
    private Integer valueInteger;
    private BigDecimal percentageValue;
} 