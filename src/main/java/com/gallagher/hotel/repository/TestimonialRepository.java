package com.gallagher.hotel.repository;

import com.gallagher.hotel.models.Testimonial;
import com.gallagher.hotel.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    
    List<Testimonial> findByUser(User user);
    
    List<Testimonial> findByApprovedTrue();
    
    Page<Testimonial> findByApprovedTrue(Pageable pageable);
    
    List<Testimonial> findByApprovedFalse();
    
    @Query("SELECT AVG(t.rating) FROM Testimonial t WHERE t.approved = true")
    Double findAverageRating();
    
    @Query("SELECT t FROM Testimonial t WHERE t.approved = true ORDER BY t.rating DESC")
    List<Testimonial> findTopRatedTestimonials(Pageable pageable);
} 