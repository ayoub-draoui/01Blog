package _blog.demo.service;

import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.model.Post;
import _blog.demo.model.Report;
import _blog.demo.model.ReportStatus;
import _blog.demo.model.User;
import _blog.demo.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AdminService {
    private UserRepository userRepo;
    private PostRepository postRepo;
    private ReportRepository reportRepo;
    private SubscriptionRepository subscriptionRepo;
    private LikeRepository likeRepo;
    private CommentRepository commentRepo;
    private NotificationRepository notificationRepo;
    private FileStorageService fileStorageService;
    // git aall users;
      public Page<User> getAllUsers(int page, int size) {
        return userRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

                // get users my username ;

     public Page<User> searchUsers(String query, int page, int size) {
        return userRepo.findByUsernameContainingIgnoreCase(query, PageRequest.of(page, size));
    }

//   deelete as welll ass all his data;

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Delete 
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            fileStorageService.deletFile(user.getAvatar());
        }

        Page<Post> userPosts = postRepo.findAllByAuthorId(userId, PageRequest.of(0, 1000));
        
        for (Post post : userPosts) {
            if (post.getMediaUrl() != null) {
                fileStorageService.deletFile(post.getMediaUrl());
            }
            likeRepo.deleteByPostId(post.getId());
            commentRepo.deleteByPostId(post.getId());
        }

        postRepo.deleteAll(userPosts);

        subscriptionRepo.deleteByFollowerId(userId);
        subscriptionRepo.deleteByFollowingId(userId);

        likeRepo.deleteByUserId(userId);

        commentRepo.deleteByUserId(userId);

        notificationRepo.deleteByUserId(userId);
        notificationRepo.deleteByActorId(userId);

        reportRepo.deleteByReporterId(userId);
        reportRepo.deleteByReportedUserId(userId);

        userRepo.delete(user);
    }


    // handle post elimination;

     @Transactional
    public void deletePost(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (post.getMediaUrl() != null) {
            fileStorageService.deletFile(post.getMediaUrl());
        }

        likeRepo.deleteByPostId(postId);

        commentRepo.deleteByPostId(postId);

        notificationRepo.deleteByRelatedPostId(postId);

        reportRepo.deleteByReportedPostId(postId);

        postRepo.delete(post);
    }



    // bring all stats for the dashboard 
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

        Map<String, Long> reportsByStatus = new HashMap<>();
        reportsByStatus.put("pending", reportRepo.countByStatus(ReportStatus.PENDING));
        reportsByStatus.put("reviewed", reportRepo.countByStatus(ReportStatus.REVIEWED));
        reportsByStatus.put("resolved", reportRepo.countByStatus(ReportStatus.RESOLVED));
        reportsByStatus.put("dismissed", reportRepo.countByStatus(ReportStatus.DISMISSED));
        stats.put("reportsByStatus", reportsByStatus);

        return stats;
    }




}
