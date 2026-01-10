package _blog.demo.dto;

import jakarta.validation.constraints.*;

public record ReportUReq(
    @NotNull(message = "Reported user ID is required")
    Long reportedUserId,
    
    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 1000, message = "Reason must be between 10 and 1000 characters")
    String reason
) {}