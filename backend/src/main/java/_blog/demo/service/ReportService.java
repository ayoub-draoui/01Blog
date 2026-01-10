package _blog.demo.service;

import _blog.demo.dto.ReportPostRequest;
import _blog.demo.dto.ReportResponse;
import _blog.demo.dto.ReportUReq;
import _blog.demo.dto.UpdateReportRequest;
import _blog.demo.exceptions.BadRequest;
import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.model.*;
import _blog.demo.repository.PostRepository;
import _blog.demo.repository.ReportRepository;
import _blog.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public Report reportUser(Long reporterId, ReportUReq request) {
        // Check if reported user exists
        if (!userRepository.existsById(request.reportedUserId())) {
            throw new ResourceNotFoundException("User not found with id: " + request.reportedUserId());
        }

        // Can't report yourself
        if (reporterId.equals(request.reportedUserId())) {
            throw new IllegalArgumentException("You cannot report yourself");
        }

        // Check if already reported
        if (reportRepository.existsByReporterIdAndReportedUserId(reporterId, request.reportedUserId())) {
            throw new IllegalArgumentException("You have already reported this user");
        }

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setReportedUserId(request.reportedUserId());
        report.setReportType(ReportType.USER);
        report.setReason(request.reason());
        report.setStatus(ReportStatus.PENDING);

        return reportRepository.save(report);
    }

    public Page<Report> getReportsByReporter(Long reporterId, int page, int size) {
        return reportRepository.findByReporterId(reporterId, PageRequest.of(page, size));
    }

    public Report reportPost(Long reporterId, ReportPostRequest request) {
        // Check if post exists
        if (!postRepository.existsById(request.reportedPostId())) {
            throw new ResourceNotFoundException("Post not found with id: " + request.reportedPostId());
        }

        // Check if already reported
        if (reportRepository.existsByReporterIdAndReportedPostId(reporterId, request.reportedPostId())) {
            throw new BadRequest("You have already reported this post");
        }

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setReportedPostId(request.reportedPostId());
        report.setReportType(ReportType.POST);
        report.setReason(request.reason());
        report.setStatus(ReportStatus.PENDING);

        return reportRepository.save(report);
    }

    // Get all reports (Admin only)
    public Page<Report> getAllReports(int page, int size) {
        return reportRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    public Page<Report> getReportsByStatus(ReportStatus status, int page, int size) {
        return reportRepository.findByStatus(status, PageRequest.of(page, size));
    }

    public Page<Report> getReportsByType(ReportType type, int page, int size) {
        return reportRepository.findByReportType(type, PageRequest.of(page, size));
    }

    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
    }

    public Report updateReport(Long reportId, Long adminId, UpdateReportRequest request) {
        Report report = getReportById(reportId);

        report.setStatus(request.status());
        if (request.adminNotes() != null) {
            report.setAdminNotes(request.adminNotes());
        }
        report.setReviewedBy(adminId);

        return reportRepository.save(report);
    }

    public void deleteReport(Long reportId) {
        Report report = getReportById(reportId);
        reportRepository.delete(report);
    }

    public long getPendingReportsCount() {
        return reportRepository.countPendingReports();
    }

    public ReportResponse enrichReport(Report report) {
        User reporter = userRepository.findById(report.getReporterId()).orElse(null);
        User reportedUser = report.getReportedUserId() != null
                ? userRepository.findById(report.getReportedUserId()).orElse(null)
                : null;
        Post reportedPost = report.getReportedPostId() != null
                ? postRepository.findById(report.getReportedPostId()).orElse(null)
                : null;
        User reviewer = report.getReviewedBy() != null
                ? userRepository.findById(report.getReviewedBy()).orElse(null)
                : null;

        return new ReportResponse(
                report.getId(),
                report.getReporterId(),
                reporter != null ? reporter.getUsername() : "Unknown",
                report.getReportedUserId(),
                reportedUser != null ? reportedUser.getUsername() : null,
                report.getReportedPostId(),
                reportedPost != null ? reportedPost.getTitle() : null,
                report.getReportType(),
                report.getReason(),
                report.getStatus(),
                report.getAdminNotes(),
                report.getReviewedBy(),
                reviewer != null ? reviewer.getUsername() : null,
                report.getCreatedAt(),
                report.getUpdatedAt());
    }

    public Page<ReportResponse> enrichReports(Page<Report> reports) {
        return reports.map(this::enrichReport);
    }

}
