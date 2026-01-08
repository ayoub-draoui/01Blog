package _blog.demo.service;

import _blog.demo.dto.PostResponse;
import _blog.demo.model.Post;
import _blog.demo.model.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostEnrichmentService {
    private UserService userService;
    private LikeService likeService;
    private CommentService commentService;

    
    public PostResponse enrichPost(Post post, Long currentUserId) {
        User author = userService.getUserById(post.getAuthorId());
        
        Long likesCount = likeService.getLikesCount(post.getId());
        Long commentsCount = commentService.getCommentsCount(post.getId());
        
        Boolean isLiked = currentUserId != null 
            ? likeService.hasUserLiked(currentUserId, post.getId())
            : false;
        
        return PostResponse.from(
            post,
            author.getUsername(),
            author.getFirstname(),
            author.getLastname(),
            author.getAvatar(),
            likesCount,
            commentsCount,
            isLiked
        );
    }
 
    public Page<PostResponse> enrichPosts(Page<Post> posts, Long currentUserId) {
        return posts.map(post -> enrichPost(post, currentUserId));
    }
}
