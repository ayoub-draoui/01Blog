package _blog.demo.controllers;

import _blog.demo.dto.PostResponse;
import _blog.demo.dto.PostUpdateRequest;
import _blog.demo.model.Post;
import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.PostService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

 
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PostResponse> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "media", required = false) MultipartFile media,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        
        Post created = postService.creatPost(post, user.getUsername(), user.getId(), media);
        
        // Fetch the enriched post data using optimized query
        PostResponse enrichedPost = postService.getPostById(created.getId(), user.getId());
        
        return ResponseEntity.ok(enrichedPost);
    }

     
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long currentUserId = user != null ? user.getId() : null;
        List<PostResponse> posts = postService.allPosts(currentUserId, page, size);
        long totalPosts = postService.getTotalPostsCount();
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", posts);
        response.put("currentPage", page);
        response.put("totalItems", totalPosts);
        response.put("totalPages", (int) Math.ceil((double) totalPosts / size));
        response.put("pageSize", size);
        
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Map<String, Object>> getPostsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long currentUserId = user != null ? user.getId() : null;
        
        // Get posts using optimized query
        List<PostResponse> posts = postService.getByAuthor(authorId, currentUserId, page, size);
        long totalPosts = postService.getTotalPostsCount(); // You might want to add countByAuthorId
        
        // Build pagination response
        Map<String, Object> response = new HashMap<>();
        response.put("content", posts);
        response.put("currentPage", page);
        response.put("totalItems", totalPosts);
        response.put("totalPages", (int) Math.ceil((double) totalPosts / size));
        response.put("pageSize", size);
        
        return ResponseEntity.ok(response);
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long currentUserId = user != null ? user.getId() : null;
        
        // Get post using optimized query
        PostResponse post = postService.getPostById(id, currentUserId);
        
        return ResponseEntity.ok(post);
    }

  
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "media", required = false) MultipartFile media,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        PostUpdateRequest request = new PostUpdateRequest(title, content);
        Post updatedPost = postService.updatePost(id, user.getId(), request, media);
        
        PostResponse enrichedPost = postService.getPostById(updatedPost.getId(), user.getId());
        
        return ResponseEntity.ok(enrichedPost);
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        postService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}