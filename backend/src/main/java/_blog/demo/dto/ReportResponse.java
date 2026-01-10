package _blog.demo.dto;

import _blog.demo.model.ReportStatus;
import _blog.demo.model.ReportType;
import java.time.LocalDateTime;

public record ReportResponse(
    Long id,
    Long reporterId,
    String reporterUsername,
    Long reportedUserId,
    String reportedUsername,
    Long reportedPostId,
    String reportedPostTitle,
    ReportType reportType,
    String reason,
    ReportStatus status,
    String adminNotes,
    Long reviewedBy,
    String reviewedByUsername,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}