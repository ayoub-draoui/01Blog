package _blog.demo.controllers;

import _blog.demo.model.Subscription;
import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.SubscriptionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subscriptions")
@AllArgsConstructor
public class SubscriptionController {
    private SubscriptionService subscriptionService;

    // Follow a user
    @PostMapping("/follow/{userId}")
    public ResponseEntity<Map<String, String>> follow(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        subscriptionService.follow(currentUser.getId(), userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully followed user");
        return ResponseEntity.ok(response);
    }

    // Unfollow a user
    @DeleteMapping("/unfollow/{userId}")
    public ResponseEntity<Map<String, String>> unfollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        subscriptionService.unfollow(currentUser.getId(), userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully unfollowed user");
        return ResponseEntity.ok(response);
    }

    // Get followers of a user
    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<Subscription>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getFollowers(userId));
    }

    // Get users that a user is following
    @GetMapping("/following/{userId}")
    public ResponseEntity<List<Subscription>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getFollowing(userId));
    }

    // Check if current user follows another user
    @GetMapping("/is-following/{userId}")
    public ResponseEntity<Map<String, Boolean>> isFollowing(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        boolean isFollowing = subscriptionService.isFollowing(currentUser.getId(), userId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFollowing", isFollowing);
        return ResponseEntity.ok(response);
    }

    // Get follower/following counts
    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Long>> getStats(@PathVariable Long userId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("followers", subscriptionService.getFollowersCount(userId));
        stats.put("following", subscriptionService.getFollowingCount(userId));
        return ResponseEntity.ok(stats);
    }
}