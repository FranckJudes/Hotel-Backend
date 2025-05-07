package com.gallagher.hotel.services;

import com.gallagher.hotel.dto.BlogPostDto;
import com.gallagher.hotel.dto.blog.CreateBlogPostRequest;
import com.gallagher.hotel.dto.blog.UpdateBlogPostRequest;
import com.gallagher.hotel.mappers.BlogPostMapper;
import com.gallagher.hotel.models.BlogPost;
import com.gallagher.hotel.models.User;
import com.gallagher.hotel.repository.BlogPostRepository;
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
public class BlogPostService {
    
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final BlogPostMapper blogPostMapper;
    
    public List<BlogPostDto> getAllPublishedPosts() {
        return blogPostRepository.findByPublishedTrueOrderByPublishedAtDesc()
                .stream()
                .map(blogPostMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public Page<BlogPostDto> getPaginatedPublishedPosts(Pageable pageable) {
        return blogPostRepository.findByPublishedTrue(pageable)
                .map(blogPostMapper::toDto);
    }
    
    public BlogPostDto getPostById(Long id) {
        BlogPost blogPost = findPostById(id);
        
        // Vérifier que le post est publié si on n'est pas l'auteur ou un admin
        if (!blogPost.isPublished()) {
            throw new AccessDeniedException("Vous n'avez pas accès à cet article");
        }
        
        return blogPostMapper.toDto(blogPost);
    }
    
    public BlogPostDto getPostByIdAdmin(Long id) {
        BlogPost blogPost = findPostById(id);
        return blogPostMapper.toDto(blogPost);
    }
    
    public Page<BlogPostDto> searchPosts(String keyword, Pageable pageable) {
        return blogPostRepository.searchBlogPosts(keyword, pageable)
                .map(blogPostMapper::toDto);
    }
    
    public Page<BlogPostDto> getPostsByTag(String tag, Pageable pageable) {
        return blogPostRepository.findByTag(tag, pageable)
                .map(blogPostMapper::toDto);
    }
    
    @Transactional
    public BlogPostDto createPost(CreateBlogPostRequest request, UserDetails userDetails) {
        User author = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé"));
        
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(request.getTitle());
        blogPost.setContent(request.getContent());
        blogPost.setAuthor(author);
        blogPost.setFeaturedImage(request.getFeaturedImage());
        blogPost.setTags(request.getTags());
        blogPost.setPublished(request.isPublished());
        
        if (request.isPublished()) {
            blogPost.setPublishedAt(LocalDateTime.now());
        }
        
        BlogPost savedPost = blogPostRepository.save(blogPost);
        return blogPostMapper.toDto(savedPost);
    }
    
    @Transactional
    public BlogPostDto updatePost(Long id, UpdateBlogPostRequest request, UserDetails userDetails) {
        BlogPost blogPost = findPostById(id);
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé"));
        
        // Vérifier si l'utilisateur est l'auteur ou un admin
        if (!blogPost.getAuthor().equals(user) && !userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cet article");
        }
        
        // Mettre à jour seulement les champs fournis dans la requête
        if (request.getTitle() != null) {
            blogPost.setTitle(request.getTitle());
        }
        
        if (request.getContent() != null) {
            blogPost.setContent(request.getContent());
        }
        
        if (request.getFeaturedImage() != null) {
            blogPost.setFeaturedImage(request.getFeaturedImage());
        }
        
        if (request.getTags() != null) {
            blogPost.setTags(request.getTags());
        }
        
        if (request.getPublished() != null) {
            boolean wasPublished = blogPost.isPublished();
            blogPost.setPublished(request.getPublished());
            
            // Si on publie pour la première fois, définir la date de publication
            if (!wasPublished && request.getPublished()) {
                blogPost.setPublishedAt(LocalDateTime.now());
            }
        }
        
        BlogPost updatedPost = blogPostRepository.save(blogPost);
        return blogPostMapper.toDto(updatedPost);
    }
    
    @Transactional
    public void deletePost(Long id, UserDetails userDetails) {
        BlogPost blogPost = findPostById(id);
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé"));
        
        // Vérifier si l'utilisateur est l'auteur ou un admin
        if (!blogPost.getAuthor().equals(user) && !userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer cet article");
        }
        
        blogPostRepository.delete(blogPost);
    }
    
    public List<BlogPostDto> getUserPosts(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé"));
        
        return blogPostRepository.findByAuthor(user)
                .stream()
                .map(blogPostMapper::toDto)
                .collect(Collectors.toList());
    }
    
    private BlogPost findPostById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Article non trouvé avec l'ID: " + id));
    }
} 