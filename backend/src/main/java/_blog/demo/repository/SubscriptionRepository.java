package _blog.demo.repository;

import _blog.demo.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    // Check if follower follows following
    Optional<Subscription> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    // Get all users that followerId follows
    List<Subscription> findByFollowerId(Long followerId);
    
    // Get all followers of followingId
    List<Subscription> findByFollowingId(Long followingId);
    
    // Count followers
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.followingId = :userId")
    long countFollowers(@Param("userId") Long userId);
    
    // Count following
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.followerId = :userId")
    long countFollowing(@Param("userId") Long userId);
    
    // Check if exists
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
}