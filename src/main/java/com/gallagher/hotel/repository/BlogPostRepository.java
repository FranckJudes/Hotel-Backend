package com.gallagher.hotel.repository;

import com.gallagher.hotel.models.BlogPost;
import com.gallagher.hotel.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    List<BlogPost> findByAuthor(User author);
    
    Page<BlogPost> findByPublishedTrue(Pageable pageable);
    
    List<BlogPost> findByPublishedTrueOrderByPublishedAtDesc();
    
    @Query("SELECT b FROM BlogPost b WHERE b.published = true AND b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<BlogPost> searchBlogPosts(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT b FROM BlogPost b JOIN b.tags t WHERE t = :tag AND b.published = true")
    Page<BlogPost> findByTag(@Param("tag") String tag, Pageable pageable);
    
    @Query("SELECT COUNT(b) FROM BlogPost b WHERE b.published = true AND b.publishedAt BETWEEN :startDate AND :endDate")
    long countPublishedPostsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 