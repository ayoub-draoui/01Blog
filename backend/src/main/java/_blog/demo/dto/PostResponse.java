package _blog.demo.dto;

import _blog.demo.model.Post;
import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long ahthorId,
        String authorUsername,
        String authorFirstname,
        String authorLastname,
        String authorAvatar,
        String mediaUrl,
        String mediaType,
        Long likesCount,
        Long commentsCount,
        Boolean isLikedByCurrentUser,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static PostResponse from(Post post, String authorUsername, String authorFirstname,
            String authorLastname, String authorAvatar,
            Long likesCount, Long commentsCount, Boolean isLiked) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthorId(),
                authorUsername,
                authorFirstname,
                authorLastname,
                authorAvatar,
                post.getMediaUrl(),
                post.getMediaType(),
                likesCount,
                commentsCount,
                isLiked,
                post.getCreatedAt(),
                post.getUpdatedAt());
    }

}
