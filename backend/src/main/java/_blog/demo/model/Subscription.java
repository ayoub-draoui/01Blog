package _blog.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "subscriptions", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "follower_id", nullable = false)
    private Long followerId; // User who follows

    @Column(name = "following_id", nullable = false)
    private Long followingId; // User being followed

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}