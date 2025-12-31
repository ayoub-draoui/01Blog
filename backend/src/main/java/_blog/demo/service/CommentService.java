package _blog.demo.service;

import _blog.demo.dto.CommentCreateRequest;
import _blog.demo.dto.CommentUpdateRequest;
import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.exceptions.UnauthorizedException;
import _blog.demo.model.Comment;
import _blog.demo.repository.CommentRepository;
import _blog.demo.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {
    private CommentRepository commentRepo;
    private PostRepository postRepo;

    public Comment createComment(Long postId, Long userId, CommentCreateRequest request) {
        // Check if post exists
        if (!postRepo.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(request.content());
        
        return commentRepo.save(comment);
    }

    public Comment updateComment(Long commentId, Long userId, CommentUpdateRequest request) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Only owner can update
        if (!comment.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this comment");
        }

        comment.setContent(request.content());
        return commentRepo.save(comment);
    }

    public void deleteComment(Long commentId, Long userId, String userRole) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Owner or admin can delete
        if (!comment.getUserId().equals(userId) && !userRole.equals("ROLE_ADMIN")) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        commentRepo.delete(comment);
    }

    public Page<Comment> getPostComments(Long postId, int page, int size) {
        return commentRepo.findByPostIdOrderByCreatedAtDesc(postId, PageRequest.of(page, size));
    }

    public List<Comment> getPostCommentsAll(Long postId) {
        return commentRepo.findByPostIdOrderByCreatedAtDesc(postId);
    }

    public long getCommentsCount(Long postId) {
        return commentRepo.countByPostId(postId);
    }

    @Transactional
    public void deleteAllCommentsForPost(Long postId) {
        commentRepo.deleteByPostId(postId);
    }
}