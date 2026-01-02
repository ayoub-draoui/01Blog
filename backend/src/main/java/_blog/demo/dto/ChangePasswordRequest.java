package _blog.demo.dto;

import jakarta.validation.constraints.*;

public record ChangePasswordRequest(
    @NotBlank(message = "Current password is required")
    String currentPassword,
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    String newPassword
) {}