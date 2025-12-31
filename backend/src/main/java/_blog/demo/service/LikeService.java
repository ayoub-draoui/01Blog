package _blog.demo.service;

import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.model.Like;
import _blog.demo.repository.LikeRepository;
import _blog.demo.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class LikeService {
    private LikeRepository likeRepo;
    private PostRepository postRepo;

    public Like likePost(Long userId, Long postId) {
        // Check if post exists
        if (!postRepo.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }

        // Check if already liked
        if (likeRepo.existsByUserIdAndPostId(userId, postId)) {
            throw new IllegalArgumentException("You have already liked this post");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setPostId(postId);
        return likeRepo.save(like);
    }

    public void unlikePost(Long userId, Long postId) {
        Like like = likeRepo.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found"));
        
        likeRepo.delete(like);
    }

    public List<Like> getPostLikes(Long postId) {
        return likeRepo.findByPostId(postId);
    }

    public long getLikesCount(Long postId) {
        return likeRepo.countByPostId(postId);
    }

    public boolean hasUserLiked(Long userId, Long postId) {
        return likeRepo.existsByUserIdAndPostId(userId, postId);
    }

    @Transactional
    public void deleteAllLikesForPost(Long postId) {
        likeRepo.deleteByPostId(postId);
    }
}