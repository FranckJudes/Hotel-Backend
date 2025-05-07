package com.gallagher.hotel.mappers;

import com.gallagher.hotel.dto.TestimonialDto;
import com.gallagher.hotel.dto.UserDto;
import com.gallagher.hotel.models.Testimonial;
import org.springframework.stereotype.Component;

@Component
public class TestimonialMapper {

    private final UserMapper userMapper;

    public TestimonialMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public TestimonialDto toDto(Testimonial testimonial) {
        if (testimonial == null) {
            return null;
        }

        UserDto userDto = null;
        if (testimonial.getUser() != null) {
            userDto = userMapper.toDto(testimonial.getUser());
        }

        return new TestimonialDto(
                testimonial.getId(),
                testimonial.getUser() != null ? testimonial.getUser().getId() : null,
                userDto,
                testimonial.getContent(),
                testimonial.getRating(),
                testimonial.isApproved(),
                testimonial.getCreatedAt(),
                testimonial.getUpdatedAt()
        );
    }

    public Testimonial toEntity(TestimonialDto dto) {
        if (dto == null) {
            return null;
        }

        Testimonial testimonial = new Testimonial();
        testimonial.setId(dto.getId());
        testimonial.setContent(dto.getContent());
        testimonial.setRating(dto.getRating());
        testimonial.setApproved(dto.isApproved());
        
        // L'utilisateur est défini séparément via la méthode setUser

        return testimonial;
    }
} 