package _blog.demo.service;

import _blog.demo.dto.PostResponse;
import _blog.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final PostRepository postRepository;
    private final PostService postService;

    /**
     * Get personalized feed - OPTIMIZED SINGLE QUERY
     * Shows posts from users that the current user follows
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPersonalizedFeed(Long currentUserId, int page, int size) {
        int offset = page * size;
        
        // Get posts using optimized query
        List<Object[]> results = postRepository.findPersonalizedFeedWithDetails(
            currentUserId, 
            size, 
            offset
        );
        
        // Convert to PostResponse
        List<PostResponse> posts = results.stream()
            .map(postService::mapToPostResponse)
            .collect(Collectors.toList());
        
        // Get total count
        long totalPosts = postRepository.countPersonalizedFeed(currentUserId);
        
        // Build pagination response
        Map<String, Object> response = new HashMap<>();
        response.put("content", posts);
        response.put("currentPage", page);
        response.put("totalItems", totalPosts);
        response.put("totalPages", (int) Math.ceil((double) totalPosts / size));
        response.put("pageSize", size);
        response.put("isEmpty", posts.isEmpty());
        
        return response;
    }

    /**
     * Get explore feed - OPTIMIZED SINGLE QUERY
     * Shows all posts, newest first (for when user doesn't follow anyone or wants to explore)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getExploreFeed(Long currentUserId, int page, int size) {
        int offset = page * size;
        
        // Get posts using optimized query (same as allPosts)
        List<Object[]> results = postRepository.findAllPostsWithDetails(
            currentUserId, 
            size, 
            offset
        );
        
        // Convert to PostResponse
        List<PostResponse> posts = results.stream()
            .map(postService::mapToPostResponse)
            .collect(Collectors.toList());
        
        // Get total count
        long totalPosts = postRepository.countAllPosts();
        
        // Build pagination response
        Map<String, Object> response = new HashMap<>();
        response.put("content", posts);
        response.put("currentPage", page);
        response.put("totalItems", totalPosts);
        response.put("totalPages", (int) Math.ceil((double) totalPosts / size));
        response.put("pageSize", size);
        
        return response;
    }

    /**
     * Get home feed with automatic fallback
     * Shows personalized feed if user follows people, otherwise shows explore feed
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getHomeFeed(Long currentUserId, int page, int size) {
        // Try personalized feed first
        Map<String, Object> personalizedFeed = getPersonalizedFeed(currentUserId, page, size);
        
        // If personalized feed is empty (user doesn't follow anyone), return explore feed
        if ((Boolean) personalizedFeed.get("isEmpty")) {
            return getExploreFeed(currentUserId, page, size);
        }
        
        return personalizedFeed;
    }
}