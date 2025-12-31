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

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(
            @Valid @RequestBody Post post,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Post created = postService.creatPost(post, user.getId());
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

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Post updatedPost = postService.updatePost(id, user.getId(), request);
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