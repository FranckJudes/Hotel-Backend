package com.gallagher.hotel.repository;

import com.gallagher.hotel.models.Message;
import com.gallagher.hotel.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findBySender(User sender);
    
    List<Message> findByRecipient(User recipient);
    
    @Query("SELECT m FROM Message m WHERE m.sender = :user OR m.recipient = :user ORDER BY m.createdAt DESC")
    List<Message> findAllUserMessages(@Param("user") User user);
    
    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1) ORDER BY m.createdAt DESC")
    List<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2);
    
    List<Message> findByRecipientAndReadFalse(User recipient);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.recipient = :user AND m.read = false")
    long countUnreadMessages(@Param("user") User user);
    
    Page<Message> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);
    
    Page<Message> findBySenderOrderByCreatedAtDesc(User sender, Pageable pageable);
} 