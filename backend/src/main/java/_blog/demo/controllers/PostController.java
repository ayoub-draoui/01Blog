package _blog.demo.controllers;

import _blog.demo.dto.PostUpdateRequest;
import _blog.demo.model.Post;
import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.PostService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
      private final ObjectMapper objectMapper;

    public PostController(PostService postService, ObjectMapper oblObjectMapper) {
        this.postService = postService;
        this.objectMapper  = oblObjectMapper;
    }

     @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Post> createPost(
             @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "media", required = false) MultipartFile media,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
          Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        Post created = postService.creatPost(post, user.getId(), media);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.allPosts(page, size));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<Post>> getPostsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.getByAuthor(authorId, page, size));
    }

      @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "media", required = false) MultipartFile media,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        PostUpdateRequest request = new PostUpdateRequest(title, content);
        Post updatedPost = postService.updatePost(id, user.getId(), request, media);
        return ResponseEntity.ok(updatedPost);
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