package _blog.demo.dto;

import java.time.LocalDateTime;

public record CommentResponse(
    Long id,
    String content,
    LocalDateTime createdAt,
    Long userId,
    String username,
    String avatar,
    String userFirstname,
    String userLastname
){

}

// export interface CommentWithUser {
//   id: number;
//   content: string;
//   createdAt: string;
//   userId: number;
//   username?: string;
//   avatar?: string;





//   postId: number;
//   updatedAt: string;
//   authorUsername: string;
// }
