package _blog.demo.controllers;

import _blog.demo.model.Like;
import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts/{postId}/likes")
@AllArgsConstructor
public class LikeController {
    private LikeService likeService;

    // Like a post
    @PostMapping
    public ResponseEntity<Map<String, String>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        likeService.likePost(currentUser.getId(), postId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post liked successfully");
        return ResponseEntity.ok(response);
    }

    // Unlike a post
    @DeleteMapping
    public ResponseEntity<Map<String, String>> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        likeService.unlikePost(currentUser.getId(), postId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post unliked successfully");
        return ResponseEntity.ok(response);
    }

    // Get all likes for a post
    @GetMapping
    public ResponseEntity<List<Like>> getPostLikes(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getPostLikes(postId));
    }

    // Get likes count
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getLikesCount(@PathVariable Long postId) {
        Map<String, Long> response = new HashMap<>();
        response.put("count", likeService.getLikesCount(postId));
        return ResponseEntity.ok(response);
    }

    // Check if current user liked the post
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkIfLiked(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        boolean hasLiked = likeService.hasUserLiked(currentUser.getId(), postId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("liked", hasLiked);
        return ResponseEntity.ok(response);
    }
}