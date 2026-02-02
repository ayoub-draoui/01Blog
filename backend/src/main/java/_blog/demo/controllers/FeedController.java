package _blog.demo.controllers;

import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for Feed operations
 * Now uses optimized single-query methods - no more N+1 problems!
 */
@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    /**
     * Get home feed with automatic fallback - OPTIMIZED
     * Shows personalized feed if user follows people, otherwise shows explore feed
     * Single query per post!
     */
    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> getHomeFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Object> feed = feedService.getHomeFeed(currentUser.getId(), page, size);
        return ResponseEntity.ok(feed);
    }

    /**
     * Get personalized feed - OPTIMIZED
     * Shows posts from users that the current user follows
     * Returns enriched PostResponse with all data in single query
     */
    @GetMapping("/personalize")
    public ResponseEntity<Map<String, Object>> getPersonalizedFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Object> feed = feedService.getPersonalizedFeed(currentUser.getId(), page, size);
        return ResponseEntity.ok(feed);
    }

    /**
     * Get explore feed - OPTIMIZED
     * Shows all posts, newest first
     * Returns enriched PostResponse with all data in single query
     */
    @GetMapping("/explore")
    public ResponseEntity<Map<String, Object>> getExploreFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Object> feed = feedService.getExploreFeed(currentUser.getId(), page, size);
        return ResponseEntity.ok(feed);
    }
}