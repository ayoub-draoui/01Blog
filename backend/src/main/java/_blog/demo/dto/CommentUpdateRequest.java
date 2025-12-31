package _blog.demo.dto;

import jakarta.validation.constraints.*;

public record CommentUpdateRequest(
    @NotBlank(message = "Comment content is required")
    @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
    String content
) {}