package _blog.demo.service;

import _blog.demo.dto.PostResponse;
import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.model.Post;
import _blog.demo.model.ReportStatus;
import _blog.demo.model.User;
import _blog.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final ReportRepository reportRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final LikeRepository likeRepo;
    private final CommentRepository commentRepo;
    private final NotificationRepository notificationRepo;
    private final FileStorageService fileStorageService;
    private final PostService postService;

    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(int page, int size) {
        return userRepo.findAll(
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    /**
     * Search users by username
     */
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String query, int page, int size) {
        return userRepo.findByUsernameContainingIgnoreCase(
            query, 
            PageRequest.of(page, size)
        );
    }

    /**
     * Get all posts with enriched data - OPTIMIZED
     * Uses single query to get all post details
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAllPosts(Long adminUserId, int page, int size) {
        int offset = page * size;
        
        // Get posts using optimized query
        List<Object[]> results = postRepo.findAllPostsWithDetails(adminUserId, size, offset);
        
        // Convert to PostResponse
        List<PostResponse> posts = results.stream()
            .map(postService::mapToPostResponse)
            .collect(Collectors.toList());
        
        // Get total count
        long totalPosts = postRepo.countAllPosts();
        
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
     * Delete user and all associated data
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Delete user's avatar
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            fileStorageService.deletFile(user.getAvatar());
        }

        // Delete all user's posts and their media
        // Page<Post> userPosts = postRepo.findAllByAuthorId(userId, PageRequest.of(0, 1000));
        
        // for (Post post : userPosts) {
        //     if (post.getMediaUrl() != null) {
        //         fileStorageService.deletFile(post.getMediaUrl());
        //     }
        //     likeRepo.deleteByPostId(post.getId());
        //     commentRepo.deleteByPostId(post.getId());
        // }

        // postRepo.deleteAll(userPosts);

        // Delete subscriptions
        subscriptionRepo.deleteByFollowerId(userId);
        subscriptionRepo.deleteByFollowingId(userId);

        // Delete likes and comments
        likeRepo.deleteByUserId(userId);
        commentRepo.deleteByUserId(userId);

        // Delete notifications
        notificationRepo.deleteByUserId(userId);
        notificationRepo.deleteByActorId(userId);

        // Delete reports
        reportRepo.deleteByReporterId(userId);
        reportRepo.deleteByReportedUserId(userId);

        // Finally delete the user
        userRepo.delete(user);
    }

    /**
     * Delete post and all associated data
     */
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepo.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // Delete media file
        if (post.getMediaUrl() != null) {
            fileStorageService.deletFile(post.getMediaUrl());
        }

        // Delete associated data
        likeRepo.deleteByPostId(postId);
        commentRepo.deleteByPostId(postId);
        notificationRepo.deleteByRelatedPostId(postId);
        reportRepo.deleteByReportedPostId(postId);

        // Delete the post
        postRepo.delete(post);
    }

    /**
     * Get dashboard statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total counts
        stats.put("totalUsers", userRepo.count());
        stats.put("totalPosts", postRepo.count());
        stats.put("totalReports", reportRepo.count());
        stats.put("pendingReports", reportRepo.countPendingReports());
        stats.put("totalLikes", likeRepo.count());
        stats.put("totalComments", commentRepo.count());
        stats.put("totalSubscriptions", subscriptionRepo.count());

        // Reports by status
        Map<String, Long> reportsByStatus = new HashMap<>();
        reportsByStatus.put("pending", reportRepo.countByStatus(ReportStatus.PENDING));
        reportsByStatus.put("reviewed", reportRepo.countByStatus(ReportStatus.REVIEWED));
        reportsByStatus.put("resolved", reportRepo.countByStatus(ReportStatus.RESOLVED));
        reportsByStatus.put("dismissed", reportRepo.countByStatus(ReportStatus.DISMISSED));
        stats.put("reportsByStatus", reportsByStatus);

        return stats;
    }
}