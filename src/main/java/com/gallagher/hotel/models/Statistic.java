package com.gallagher.hotel.models;

import com.gallagher.hotel.enums.StatisticType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private StatisticType type;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal value;
    
    private String valueString;
    
    private Integer valueInteger;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal percentageValue;
    
    public static Statistic createRevenueStat(YearMonth yearMonth, BigDecimal amount) {
        Statistic stat = new Statistic();
        stat.setType(StatisticType.REVENUE);
        stat.setDate(yearMonth.atDay(1));
        stat.setValue(amount);
        return stat;
    }
    
    public static Statistic createOccupancyStat(YearMonth yearMonth, BigDecimal rate) {
        Statistic stat = new Statistic();
        stat.setType(StatisticType.OCCUPANCY_RATE);
        stat.setDate(yearMonth.atDay(1));
        stat.setPercentageValue(rate);
        return stat;
    }
} 