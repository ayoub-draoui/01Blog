package _blog.demo.controllers;

import _blog.demo.dto.PostResponse;
import _blog.demo.model.Post;
import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.FeedService;
import _blog.demo.service.PostEnrichmentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
@AllArgsConstructor
public class FeedController {
    private FeedService feedService;
    private PostEnrichmentService postEnrichmentService;
    
    @GetMapping("/home")
       public ResponseEntity<Page<PostResponse>> getHomeFeed(
               @RequestParam(defaultValue = "0") int page,
               @RequestParam(defaultValue = "10") int size,
               @AuthenticationPrincipal CustomUserDetails currentUser) {
           
           Page<Post> posts = feedService.getPersonalizedFeed(currentUser.getId(), page, size);
           Page<PostResponse> enrichedPosts = postEnrichmentService.enrichPosts(posts, currentUser.getId());
           
           return ResponseEntity.ok(enrichedPosts);
       }
    @GetMapping("/personalize")
    public  ResponseEntity<Page<Post>> getPersonalizedFeed(  
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Page<Post> feed = feedService.getPersonalizedFeed(currentUser.getId(), page, size);
        return ResponseEntity.ok(feed);

    }
   @GetMapping("/explore")
    public ResponseEntity<Page<PostResponse>> getExploreFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Page<Post> posts = feedService.getExploreFeed(page, size);
        Page<PostResponse> enrichedPosts = postEnrichmentService.enrichPosts(posts, currentUser.getId());
        
        return ResponseEntity.ok(enrichedPosts);
    }
}