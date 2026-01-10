package _blog.demo.dto;

import jakarta.validation.constraints.*;

public record ReportPostRequest(
    @NotNull(message = "Reported post ID is required")
    Long reportedPostId,
    
    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 1000, message = "Reason must be between 10 and 1000 characters")
    String reason
) {}