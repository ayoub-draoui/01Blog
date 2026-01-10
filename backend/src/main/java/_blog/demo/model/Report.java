package _blog.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;  

    @Column(name = "reported_user_id")
    private Long reportedUserId; 

    @Column(name = "reported_post_id")
    private Long reportedPostId; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;  

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;  

    @Column(columnDefinition = "TEXT")
    private String adminNotes; 

    @Column(name = "reviewed_by")
    private Long reviewedBy; 

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.status == null) {
            this.status = ReportStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}