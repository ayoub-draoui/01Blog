package _blog.demo.dto;

import _blog.demo.model.ReportStatus;
import jakarta.validation.constraints.*;

public record UpdateReportRequest(
    @NotNull(message = "Status is required")
    ReportStatus status,
    
    @Size(max = 1000, message = "Admin notes cannot exceed 1000 characters")
    String adminNotes
) {}