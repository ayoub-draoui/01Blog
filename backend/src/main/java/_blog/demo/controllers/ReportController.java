package _blog.demo.controllers;

import _blog.demo.dto.ReportPostRequest;
import _blog.demo.dto.ReportResponse;
import _blog.demo.dto.ReportUReq;
import _blog.demo.model.Report;
import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.ReportService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@AllArgsConstructor
public class ReportController {
    private ReportService reportService;

    // Report a user
    @PostMapping("/user")
    public ResponseEntity<Report> reportUser(
            @Valid @RequestBody ReportUReq request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Report report = reportService.reportUser(currentUser.getId(), request);
        return ResponseEntity.ok(report);
    }

    // Report a post
    @PostMapping("/post")
    public ResponseEntity<Report> reportPost(
            @Valid @RequestBody ReportPostRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Report report = reportService.reportPost(currentUser.getId(), request);
        return ResponseEntity.ok(report);
    }

    // Get my submitted reports
    @GetMapping("/my-reports")
    public ResponseEntity<Page<ReportResponse>> getMyReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        // You'll need to add this method to ReportService
        Page<Report> reports = reportService.getReportsByReporter(currentUser.getId(), page, size);
        Page<ReportResponse> enrichedReports = reportService.enrichReports(reports);
        
        return ResponseEntity.ok(enrichedReports);
    }
}