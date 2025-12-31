package _blog.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostUpdateRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 300, message = "Title cannot exceed 300 characters")
    String title,
    
    @NotBlank(message = "Content is required")
    @Size(max = 6000, message = "Content cannot exceed 6000 characters")
    String content
) {
    
}
