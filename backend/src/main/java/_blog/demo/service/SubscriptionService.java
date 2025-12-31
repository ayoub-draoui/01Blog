package _blog.demo.service;

import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.exceptions.UserAlreadyExistsException;
import _blog.demo.model.Subscription;
import _blog.demo.repository.SubscriptionRepository;
import _blog.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SubscriptionService {
    private SubscriptionRepository subscriptionRepo;
    private UserRepository userRepo;

    public Subscription follow(Long followerId, Long followingId) {
        // Check if trying to follow self
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        // Check if following user exists
        if (!userRepo.existsById(followingId)) {
            throw new ResourceNotFoundException("User not found with id: " + followingId);
        }

        // Check if already following
        if (subscriptionRepo.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new UserAlreadyExistsException("You are already following this user");
        }

        Subscription subscription = new Subscription();
        subscription.setFollowerId(followerId);
        subscription.setFollowingId(followingId);
        return subscriptionRepo.save(subscription);
    }

    public void unfollow(Long followerId, Long followingId) {
        Subscription subscription = subscriptionRepo
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
        
        subscriptionRepo.delete(subscription);
    }

    public List<Subscription> getFollowers(Long userId) {
        return subscriptionRepo.findByFollowingId(userId);
    }

    public List<Subscription> getFollowing(Long userId) {
        return subscriptionRepo.findByFollowerId(userId);
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        return subscriptionRepo.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public long getFollowersCount(Long userId) {
        return subscriptionRepo.countFollowers(userId);
    }

    public long getFollowingCount(Long userId) {
        return subscriptionRepo.countFollowing(userId);
    }
}