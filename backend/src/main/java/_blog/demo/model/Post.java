package _blog.demo.model;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name =  "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    
}
