package com.gallagher.hotel.services;

import com.gallagher.hotel.dto.TestimonialDto;
import com.gallagher.hotel.mappers.TestimonialMapper;
import com.gallagher.hotel.models.Testimonial;
import com.gallagher.hotel.models.User;
import com.gallagher.hotel.repository.TestimonialRepository;
import com.gallagher.hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final UserRepository userRepository;
    private final TestimonialMapper testimonialMapper;

    public List<TestimonialDto> getAllApprovedTestimonials() {
        return testimonialRepository.findByApprovedTrue()
                .stream()
                .map(testimonialMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<TestimonialDto> getPaginatedApprovedTestimonials(Pageable pageable) {
        return testimonialRepository.findByApprovedTrue(pageable)
                .map(testimonialMapper::toDto);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<TestimonialDto> getPendingTestimonials() {
        return testimonialRepository.findByApprovedFalse()
                .stream()
                .map(testimonialMapper::toDto)
                .collect(Collectors.toList());
    }

    public TestimonialDto getTestimonialById(Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Témoignage non trouvé avec l'ID: " + id));
        return testimonialMapper.toDto(testimonial);
    }

    @Transactional
    public TestimonialDto createTestimonial(TestimonialDto testimonialDto, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Testimonial testimonial = testimonialMapper.toEntity(testimonialDto);
        testimonial.setUser(user);
        testimonial.setApproved(false);  // Les témoignages nécessitent une approbation
        testimonial.setCreatedAt(LocalDateTime.now());
        testimonial.setUpdatedAt(LocalDateTime.now());

        Testimonial savedTestimonial = testimonialRepository.save(testimonial);
        return testimonialMapper.toDto(savedTestimonial);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public TestimonialDto approveTestimonial(Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Témoignage non trouvé avec l'ID: " + id));
        
        testimonial.setApproved(true);
        testimonial.setUpdatedAt(LocalDateTime.now());
        
        Testimonial approvedTestimonial = testimonialRepository.save(testimonial);
        return testimonialMapper.toDto(approvedTestimonial);
    }

    @Transactional
    public TestimonialDto updateTestimonial(Long id, TestimonialDto testimonialDto, UserDetails userDetails) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Témoignage non trouvé avec l'ID: " + id));
        
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est autorisé à modifier ce témoignage
        boolean isAdmin = user.getRole().name().equals("ADMIN") || 
                         user.getRole().name().equals("MANAGER");
        
        if (!isAdmin && !testimonial.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier ce témoignage");
        }

        // Mise à jour des champs modifiables
        if (testimonialDto.getContent() != null) {
            testimonial.setContent(testimonialDto.getContent());
        }
        
        if (testimonialDto.getRating() > 0) {
            testimonial.setRating(testimonialDto.getRating());
        }
        
        // Si modification par un client, nécessite une nouvelle approbation
        if (!isAdmin) {
            testimonial.setApproved(false);
        }
        
        testimonial.setUpdatedAt(LocalDateTime.now());
        
        Testimonial updatedTestimonial = testimonialRepository.save(testimonial);
        return testimonialMapper.toDto(updatedTestimonial);
    }

    @Transactional
    public void deleteTestimonial(Long id, UserDetails userDetails) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Témoignage non trouvé avec l'ID: " + id));
        
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est autorisé à supprimer ce témoignage
        boolean isAdmin = user.getRole().name().equals("ADMIN") || 
                         user.getRole().name().equals("MANAGER");
        
        if (!isAdmin && !testimonial.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer ce témoignage");
        }

        testimonialRepository.delete(testimonial);
    }

    public List<TestimonialDto> getUserTestimonials(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
                
        return testimonialRepository.findByUser(user)
                .stream()
                .map(testimonialMapper::toDto)
                .collect(Collectors.toList());
    }

    public Double getAverageRating() {
        return testimonialRepository.findAverageRating();
    }
} 