package _blog.demo.repository;

import _blog.demo.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Get all comments for a post (with pagination)
    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);
    
    // Get all comments for a post (no pagination)
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
    
    // Count comments for a post
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.postId = :postId")
    long countByPostId(@Param("postId") Long postId);
    
    // Delete all comments for a post
    void deleteByPostId(Long postId);
}