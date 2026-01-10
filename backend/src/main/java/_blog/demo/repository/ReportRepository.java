package _blog.demo.repository;

import _blog.demo.model.Report;
import _blog.demo.model.ReportStatus;
import _blog.demo.model.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    
    Page<Report> findByStatus( ReportStatus status, Pageable pageable);
    
    Page<Report> findByReportType(ReportType reportType, Pageable pageable);
    
    Page<Report> findByReportedUserId(Long userId, Pageable pageable);
    
    Page<Report> findByReportedPostId(Long postId, Pageable pageable);
    
    Page<Report> findByReporterId(Long reporterId, Pageable pageable);
    
    @Query("SELECT COUNT(r)  FROM Report r WHERE r.status = 'PENDING'")
    long countPendingReports();
    
    boolean existsByReporterIdAndReportedUserId(Long reporterId, Long reportedUserId);
    
    boolean existsByReporterIdAndReportedPostId(Long reporterId, Long reportedPostId);
    
    Page<Report> findAllByOrderByCreatedAtDesc(Pageable pageable);
}