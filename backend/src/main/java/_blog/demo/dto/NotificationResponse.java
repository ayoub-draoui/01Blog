package _blog.demo.dto;

import _blog.demo.model.NotificationType;
import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    Long userId,
    Long actorId,
    String actorUsername,
    String actorFirstname,
    String actorLastname,
    String actorAvatar,
    NotificationType type,
    Long relatedPostId,
    String relatedPostTitle,
    Long relatedCommentId,
    String message,
    Boolean isRead,
    LocalDateTime createdAt
) {}