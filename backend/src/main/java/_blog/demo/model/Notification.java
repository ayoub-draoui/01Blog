package _blog.demo.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")

public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     @Column(name = "user_id", nullable = false)
    private Long userId; 


      @Column(name = "actor_id", nullable = false)
    private Long actorId;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;


    @Column(name = "related_post_id")
    private Long relatedPostId;

      @Column(name = "related_comment_id")
    private Long relatedCommentId;


     @Column(nullable = false, columnDefinition = "TEXT")
    private String message; 

     @Column(nullable = false)
    private Boolean isRead = false; 


     @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
}
