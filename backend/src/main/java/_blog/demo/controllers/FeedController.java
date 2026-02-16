package _blog.demo.controllers;

import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

 
@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

  
    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> getHomeFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Object> feed = feedService.getHomeFeed(currentUser.getId(), page, size);
        return ResponseEntity.ok(feed);
    }
 
    @GetMapping("/personalize")
    public ResponseEntity<Map<String, Object>> getPersonalizedFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Object> feed = feedService.getPersonalizedFeed(currentUser.getId(), page, size);
        return ResponseEntity.ok(feed);
    }

    
    @GetMapping("/explore")
    public ResponseEntity<Map<String, Object>> getExploreFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Object> feed = feedService.getExploreFeed(currentUser.getId(), page, size);
        return ResponseEntity.ok(feed);
    }
}