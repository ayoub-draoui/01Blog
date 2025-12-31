package _blog.demo.controllers;

import _blog.demo.dto.CommentCreateRequest;
import _blog.demo.dto.CommentUpdateRequest;
import _blog.demo.model.Comment;
import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts/{postId}/comments")
@AllArgsConstructor
public class CommentController {
    private CommentService commentService;

    // Create a comment
    @PostMapping
    public ResponseEntity<Comment> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Comment comment = commentService.createComment(postId, currentUser.getId(), request);
        return ResponseEntity.ok(comment);
    }

    // Update a comment
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Comment comment = commentService.updateComment(commentId, currentUser.getId(), request);
        return ResponseEntity.ok(comment);
    }

    // Delete a comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        String role = currentUser.getAuthorities().iterator().next().getAuthority();
        commentService.deleteComment(commentId, currentUser.getId(), role);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Get all comments for a post (paginated)
    @GetMapping
    public ResponseEntity<Page<Comment>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return ResponseEntity.ok(commentService.getPostComments(postId, page, size));
    }

    // Get all comments without pagination
    @GetMapping("/all")
    public ResponseEntity<List<Comment>> getAllPostComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getPostCommentsAll(postId));
    }

    // Get comments count
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCommentsCount(@PathVariable Long postId) {
        Map<String, Long> response = new HashMap<>();
        response.put("count", commentService.getCommentsCount(postId));
        return ResponseEntity.ok(response);
    }
}