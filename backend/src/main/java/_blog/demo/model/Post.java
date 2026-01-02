package _blog.demo.model;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name =  "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable =  false , length = 300)
    private String title;

    @Column(nullable =  false , length =  6000)
    private String content;

    // @ManyToOne(fetch =  FetchType.LAZY)
    // @JoinColumn(name = "user_id", nullable =  false)
    @Column(nullable =  false)
    private Long authorId;

    @Column(length = 500)
    private String mediaUrl; 
    
    @Column(length = 20)
    private String mediaType; //  this field suppose to hold one of three values image || video || null; 


    @Column(nullable = false , updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void oncreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    @PreUpdate
    protected void onceUpdated(){
        this.updatedAt = LocalDateTime.now();
    }
}
