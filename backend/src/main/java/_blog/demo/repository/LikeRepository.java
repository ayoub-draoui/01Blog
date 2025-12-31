package _blog.demo.repository;

import _blog.demo.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    
    // Find like by user and post
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    
    // Get all likes for a post
    List<Like> findByPostId(Long postId);
    
    // Count likes for a post
    @Query("SELECT COUNT(l) FROM Like l WHERE l.postId = :postId")
    long countByPostId(@Param("postId") Long postId);
    
    // Check if user liked a post
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    
    // Delete all likes for a post (when post is deleted)
    void deleteByPostId(Long postId);
}