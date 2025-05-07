package com.gallagher.hotel.mappers;

import com.gallagher.hotel.dto.BlogPostDto;
import com.gallagher.hotel.dto.UserDto;
import com.gallagher.hotel.models.BlogPost;
import com.gallagher.hotel.models.User;
import org.springframework.stereotype.Component;

@Component
public class BlogPostMapper {

    public BlogPostDto toDto(BlogPost blogPost) {
        if (blogPost == null) {
            return null;
        }
        
        return BlogPostDto.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .content(blogPost.getContent())
                .authorId(blogPost.getAuthor().getId())
                .author(mapUserToDto(blogPost.getAuthor()))
                .featuredImage(blogPost.getFeaturedImage())
                .tags(blogPost.getTags())
                .published(blogPost.isPublished())
                .publishedAt(blogPost.getPublishedAt())
                .createdAt(blogPost.getCreatedAt())
                .updatedAt(blogPost.getUpdatedAt())
                .build();
    }

    public BlogPost toEntity(BlogPostDto blogPostDto) {
        if (blogPostDto == null) {
            return null;
        }
        
        BlogPost blogPost = new BlogPost();
        blogPost.setId(blogPostDto.getId());
        blogPost.setTitle(blogPostDto.getTitle());
        blogPost.setContent(blogPostDto.getContent());
        blogPost.setFeaturedImage(blogPostDto.getFeaturedImage());
        blogPost.setTags(blogPostDto.getTags());
        blogPost.setPublished(blogPostDto.isPublished());
        blogPost.setPublishedAt(blogPostDto.getPublishedAt());
        blogPost.setCreatedAt(blogPostDto.getCreatedAt());
        blogPost.setUpdatedAt(blogPostDto.getUpdatedAt());
        
        return blogPost;
    }
    
    private UserDto mapUserToDto(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }
} 