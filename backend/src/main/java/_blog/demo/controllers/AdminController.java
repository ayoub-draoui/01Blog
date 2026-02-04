package _blog.demo.controllers;

import _blog.demo.dto.ReportResponse;
import _blog.demo.dto.UpdateReportRequest;
import _blog.demo.dto.UserProfileResponse;
import _blog.demo.model.Report;
import _blog.demo.model.ReportStatus;
import _blog.demo.model.User;
import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

 
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final ReportService reportService;
    private final AdminService adminService;

   
    
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        System.out.println("IM HERE THIS IS COMMING FROM ADMIN CONNTROLLER BACK-END");
        Page<User> users = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(users);
    }

    
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserProfileResponse> getUserById(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        UserProfileResponse user = userService.getUserProfile(userId, currentUser.getId());
        return ResponseEntity.ok(user);
    }

    
    @GetMapping("/users/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<User> users = adminService.searchUsers(query, page, size);
        return ResponseEntity.ok(users);
    }

     
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Map<String, Object> posts = adminService.getAllPosts(currentUser.getId(), page, size);
        return ResponseEntity.ok(posts);
    }
 
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId) {
        adminService.deletePost(postId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post deleted successfully");
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/reports")
    public ResponseEntity<Page<ReportResponse>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<Report> reports = reportService.getAllReports(page, size);
        Page<ReportResponse> enriched = reportService.enrichReports(reports);
        return ResponseEntity.ok(enriched);
    }

    
    @GetMapping("/reports/status/{status}")
    public ResponseEntity<Page<ReportResponse>> getReportsByStatus(
            @PathVariable ReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<Report> reports = reportService.getReportsByStatus(status, page, size);
        Page<ReportResponse> enriched = reportService.enrichReports(reports);
        return ResponseEntity.ok(enriched);
    }

    
    @GetMapping("/reports/pending")
    public ResponseEntity<Page<ReportResponse>> getPendingReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<Report> reports = reportService.getReportsByStatus(ReportStatus.PENDING, page, size);
        Page<ReportResponse> enriched = reportService.enrichReports(reports);
        return ResponseEntity.ok(enriched);
    }

    
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable Long reportId) {
        Report report = reportService.getReportById(reportId);
        ReportResponse enriched = reportService.enrichReport(report);
        return ResponseEntity.ok(enriched);
    }

    
    @PutMapping("/reports/{reportId}")
    public ResponseEntity<Report> updateReport(
            @PathVariable Long reportId,
            @Valid @RequestBody UpdateReportRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Report updated = reportService.updateReport(reportId, currentUser.getId(), request);
        return ResponseEntity.ok(updated);
    }
 
    @DeleteMapping("/reports/{reportId}")
    public ResponseEntity<Map<String, String>> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Report deleted successfully");
        return ResponseEntity.ok(response);
    }

     
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}