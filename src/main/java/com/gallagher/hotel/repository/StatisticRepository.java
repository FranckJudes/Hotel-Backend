package com.gallagher.hotel.repository;

import com.gallagher.hotel.enums.StatisticType;
import com.gallagher.hotel.models.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {
    
    List<Statistic> findByType(StatisticType type);
    
    List<Statistic> findByTypeAndDateBetween(StatisticType type, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT s FROM Statistic s WHERE s.type = :type AND YEAR(s.date) = :year AND MONTH(s.date) = :month")
    List<Statistic> findByTypeAndYearAndMonth(@Param("type") StatisticType type, @Param("year") int year, @Param("month") int month);
    
    @Query("SELECT s FROM Statistic s WHERE s.type = :type AND YEAR(s.date) = :year ORDER BY s.date")
    List<Statistic> findByTypeAndYear(@Param("type") StatisticType type, @Param("year") int year);
}