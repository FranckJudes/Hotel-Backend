package com.gallagher.hotel.services;

import com.gallagher.hotel.dto.StatisticDto;
import com.gallagher.hotel.enums.StatisticType;
import com.gallagher.hotel.mappers.StatisticMapper;
import com.gallagher.hotel.models.Reservation;
import com.gallagher.hotel.models.Room;
import com.gallagher.hotel.models.Statistic;
import com.gallagher.hotel.repository.PaymentRepository;
import com.gallagher.hotel.repository.ReservationRepository;
import com.gallagher.hotel.repository.RoomRepository;
import com.gallagher.hotel.repository.StatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final StatisticRepository statisticRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final StatisticMapper statisticMapper;

    public List<StatisticDto> getStatisticsByTypeAndPeriod(StatisticType type, LocalDate startDate, LocalDate endDate) {
        return statisticRepository.findByTypeAndDateBetween(type, startDate, endDate)
                .stream()
                .map(statisticMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public Map<Integer, BigDecimal> getMonthlyRevenuesForYear(int year) {
        Map<Integer, BigDecimal> monthlyRevenues = new HashMap<>();
        
        for (int month = 1; month <= 12; month++) {
            BigDecimal revenue = paymentRepository.sumCompletedPaymentsForMonth(month, year);
            monthlyRevenues.put(month, revenue != null ? revenue : BigDecimal.ZERO);
        }
        
        return monthlyRevenues;
    }
    
    public Map<String, Object> calculateOccupancyRate(LocalDate startDate, LocalDate endDate) {
        // Total des chambres disponibles
        long totalRooms = roomRepository.count();
        
        // Nombre de jours dans la période
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // Nombre total de nuits disponibles dans la période
        long totalAvailableNights = totalRooms * totalDays;
        
        // Récupérer toutes les réservations dans la période
        List<Reservation> reservations = reservationRepository.findByCheckInDateBetween(startDate, endDate);
        
        // Calculer le nombre total de nuits réservées
        long totalBookedNights = 0;
        for (Reservation reservation : reservations) {
            LocalDate checkin = reservation.getCheckInDate();
            LocalDate checkout = reservation.getCheckOutDate();
            
            // Si la réservation débute avant la période, ajuster à la date de début
            if (checkin.isBefore(startDate)) {
                checkin = startDate;
            }
            
            // Si la réservation se termine après la période, ajuster à la date de fin
            if (checkout.isAfter(endDate)) {
                checkout = endDate;
            }
            
            // Calculer le nombre de nuits pour cette réservation
            long nights = ChronoUnit.DAYS.between(checkin, checkout);
            totalBookedNights += nights;
        }
        
        // Calculer le taux d'occupation
        double occupancyRate = (double) totalBookedNights / totalAvailableNights;
        BigDecimal rate = new BigDecimal(occupancyRate).multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
        
        // Créer et retourner le résultat
        Map<String, Object> result = new HashMap<>();
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("totalRooms", totalRooms);
        result.put("totalAvailableNights", totalAvailableNights);
        result.put("totalBookedNights", totalBookedNights);
        result.put("occupancyRate", rate);
        
        return result;
    }
    
    public Long getMonthlyReservationsCount(YearMonth yearMonth) {
        return reservationRepository.countConfirmedReservationsForMonth(yearMonth.getYear(), yearMonth.getMonthValue());
    }
    
    @Transactional
    public void saveMonthlyStatistics(YearMonth yearMonth) {
        // Récupérer les données du mois
        int year = yearMonth.getYear();
        int month = yearMonth.getMonthValue();
        
        // Revenus du mois
        BigDecimal monthlyRevenue = paymentRepository.sumCompletedPaymentsForMonth(month, year);
        if (monthlyRevenue != null) {
            Statistic revenueStat = Statistic.createRevenueStat(yearMonth, monthlyRevenue);
            statisticRepository.save(revenueStat);
        }
        
        // Taux d'occupation
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        Map<String, Object> occupancyData = calculateOccupancyRate(startDate, endDate);
        BigDecimal occupancyRate = (BigDecimal) occupancyData.get("occupancyRate");
        
        Statistic occupancyStat = Statistic.createOccupancyStat(yearMonth, occupancyRate);
        statisticRepository.save(occupancyStat);
        
        // Nombre de réservations
        long reservationsCount = getMonthlyReservationsCount(yearMonth);
        
        Statistic bookingsStat = new Statistic();
        bookingsStat.setType(StatisticType.BOOKINGS_COUNT);
        bookingsStat.setDate(yearMonth.atDay(1));
        bookingsStat.setValueInteger((int) reservationsCount);
        statisticRepository.save(bookingsStat);
    }
    
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> dashboardStats = new HashMap<>();
        
        // Revenus du mois en cours
        YearMonth currentMonth = YearMonth.now();
        BigDecimal currentMonthRevenue = paymentRepository.sumCompletedPaymentsForMonth(
                currentMonth.getMonthValue(), currentMonth.getYear());
        dashboardStats.put("currentMonthRevenue", currentMonthRevenue != null ? currentMonthRevenue : BigDecimal.ZERO);
        
        // Taux d'occupation du mois
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        Map<String, Object> occupancyData = calculateOccupancyRate(startDate, endDate);
        dashboardStats.put("currentMonthOccupancy", occupancyData.get("occupancyRate"));
        
        // Nombre total de chambres
        long totalRooms = roomRepository.count();
        dashboardStats.put("totalRooms", totalRooms);
        
        // Nombre de chambres disponibles aujourd'hui
        LocalDate today = LocalDate.now();
        List<Room> availableRooms = roomRepository.findAvailableRooms(today, today.plusDays(1));
        dashboardStats.put("availableRoomsToday", availableRooms.size());
        
        // Réservations du jour
        List<Reservation> todaysReservations = reservationRepository.findByCheckInOrCheckOutDate(today);
        dashboardStats.put("checkInsToday", todaysReservations.stream()
                .filter(r -> r.getCheckInDate().equals(today)).count());
        dashboardStats.put("checkOutsToday", todaysReservations.stream()
                .filter(r -> r.getCheckOutDate().equals(today)).count());
        
        return dashboardStats;
    }
} 