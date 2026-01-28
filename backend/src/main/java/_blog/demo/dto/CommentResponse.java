package _blog.demo.dto;

import java.time.LocalDateTime;

public record CommentResponse(
    Long id,
    String content,
    LocalDateTime createdAt,
    Long userId,
    String username,
    String avatar
){

}
