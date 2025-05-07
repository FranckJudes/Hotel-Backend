package com.gallagher.hotel.services;

import com.gallagher.hotel.dto.MessageDto;
import com.gallagher.hotel.dto.message.CreateMessageRequest;
import com.gallagher.hotel.dto.message.UpdateMessageStatusRequest;
import com.gallagher.hotel.models.Message;
import com.gallagher.hotel.models.User;
import com.gallagher.hotel.repository.MessageRepository;
import com.gallagher.hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    
    public MessageDto getMessageById(Long id, UserDetails userDetails) {
        Message message = findMessageById(id);
        User user = getUserFromUserDetails(userDetails);
        
        // Vérifier si l'utilisateur est l'expéditeur ou le destinataire
        if (!message.getSender().equals(user) && !message.getRecipient().equals(user)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à accéder à ce message");
        }
        
        return mapToDto(message);
    }
    
    public List<MessageDto> getReceivedMessages(UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        
        return messageRepository.findByRecipient(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<MessageDto> getSentMessages(UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        
        return messageRepository.findBySender(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public Page<MessageDto> getPaginatedReceivedMessages(UserDetails userDetails, Pageable pageable) {
        User user = getUserFromUserDetails(userDetails);
        
        return messageRepository.findByRecipientOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToDto);
    }
    
    public Page<MessageDto> getPaginatedSentMessages(UserDetails userDetails, Pageable pageable) {
        User user = getUserFromUserDetails(userDetails);
        
        return messageRepository.findBySenderOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToDto);
    }
    
    public List<MessageDto> getAllUserMessages(UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        
        return messageRepository.findAllUserMessages(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<MessageDto> getConversation(Long otherUserId, UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé avec l'ID: " + otherUserId));
        
        return messageRepository.findConversation(user, otherUser)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public long countUnreadMessages(UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        return messageRepository.countUnreadMessages(user);
    }
    
    @Transactional
    public MessageDto createMessage(CreateMessageRequest request, UserDetails userDetails) {
        User sender = getUserFromUserDetails(userDetails);
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new NoSuchElementException("Destinataire non trouvé avec l'ID: " + request.getRecipientId()));
        
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setSubject(request.getSubject());
        message.setContent(request.getContent());
        message.setRead(false);
        
        Message savedMessage = messageRepository.save(message);
        return mapToDto(savedMessage);
    }
    
    @Transactional
    public MessageDto updateMessageStatus(Long id, UpdateMessageStatusRequest request, UserDetails userDetails) {
        Message message = findMessageById(id);
        User user = getUserFromUserDetails(userDetails);
        
        // Vérifier si l'utilisateur est le destinataire
        if (!message.getRecipient().equals(user)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier le statut de ce message");
        }
        
        message.setRead(request.getRead());
        
        if (request.getRead() && message.getReadAt() == null) {
            message.setReadAt(LocalDateTime.now());
        }
        
        Message updatedMessage = messageRepository.save(message);
        return mapToDto(updatedMessage);
    }
    
    @Transactional
    public void deleteMessage(Long id, UserDetails userDetails) {
        Message message = findMessageById(id);
        User user = getUserFromUserDetails(userDetails);
        
        // Vérifier si l'utilisateur est l'expéditeur ou le destinataire
        if (!message.getSender().equals(user) && !message.getRecipient().equals(user)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer ce message");
        }
        
        messageRepository.delete(message);
    }
    
    private Message findMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message non trouvé avec l'ID: " + id));
    }
    
    private User getUserFromUserDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé"));
    }
    
    private MessageDto mapToDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .sender(mapUserToDto(message.getSender()))
                .recipientId(message.getRecipient().getId())
                .recipient(mapUserToDto(message.getRecipient()))
                .subject(message.getSubject())
                .content(message.getContent())
                .read(message.isRead())
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .build();
    }
    
    private com.gallagher.hotel.dto.UserDto mapUserToDto(User user) {
        return com.gallagher.hotel.dto.UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
} 