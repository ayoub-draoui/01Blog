package _blog.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "\"users\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique =  true , nullable =  false)
    private String email;
   
    @Column(nullable =  false)
    private String firstname;
   
    @Column(nullable =  false)
    private String lastname;
   
    @Column( nullable =  false , columnDefinition = "TEXT DEFAULT ''")
    private String avatar ;
   
   @Column(columnDefinition = "TEXT")
   private String bio;

   @Column(length = 100)
   private String location;
   
   @Column(length =  100)
   private String website;

   
   
   @Column(nullable = false)
   private String password;
   
   
   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   private Role role;
   
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
