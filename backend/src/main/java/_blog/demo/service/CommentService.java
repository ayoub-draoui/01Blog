package _blog.demo.service;

import _blog.demo.dto.CommentCreateRequest;
import _blog.demo.dto.CommentResponse;
// import _blog.demo.dto.CommentUpdateRequest;
import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.exceptions.UnauthorizedException;
import _blog.demo.model.Comment;
import _blog.demo.model.User;
import _blog.demo.repository.CommentRepository;
import _blog.demo.repository.PostRepository;
import _blog.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private CommentRepository commentRepo;
    private PostRepository postRepo;
    private UserRepository userRep;
    private NotificationService notificationService;

    public CommentResponse createComment(Long postId,String username, Long userId, CommentCreateRequest request) {
        // Check if post exists
        if (!postRepo.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        User user = userRep.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));


        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUser(user);
        // comment.setUserId(userId);
        // comment.setAuthorUsername(username);
        comment.setContent(request.content());

        Comment saved = commentRepo.save(comment);
        notificationService.createCommentNotification(postId, saved.getId(), userId);
        return mapToResponse(saved);
    }

    // public Comment updateComment(Long commentId, Long userId, CommentUpdateRequest request) {
    //     Comment comment = commentRepo.findById(commentId)
    //             .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

    //     // Only owner can update
    //     if (!comment.getUserId().equals(userId)) {
    //         throw new UnauthorizedException("You are not authorized to update this comment");
    //     }

    //     comment.setContent(request.content());
    //     return commentRepo.save(comment);
    // }

    public void deleteComment(Long commentId, Long userId, String userRole) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Owner or admin can delete
        if (!comment.getUser().getId().equals(userId) && !userRole.equals("ROLE_ADMIN")) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }
        commentRepo.delete(comment);
    }

    public Page<CommentResponse> getPostComments(Long postId, int page, int size) {
        Page<Comment> comments = commentRepo.findByPostIdOrderByCreatedAtDesc(postId, PageRequest.of(page, size));
        return comments.map(this::mapToResponse);
    }
   public List<CommentResponse> getPostCommentsAll(Long postId) {
        List<Comment> comments = commentRepo.findByPostIdOrderByCreatedAtDesc(postId);
        return comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    public long getCommentsCount(Long postId) {
        return commentRepo.countByPostId(postId);
    }

    @Transactional
    public void deleteAllCommentsForPost(Long postId) {
        commentRepo.deleteByPostId(postId);
    }
     private CommentResponse mapToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getAvatar()
        );
    }
}