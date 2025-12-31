package _blog.demo.dto;

import jakarta.validation.constraints.*;

public record CommentCreateRequest(
    @NotBlank(message = "Comment content is required")
    @Size(max = 2000, message = "the limit for a comment is 2000 chars")
    String content
) {}