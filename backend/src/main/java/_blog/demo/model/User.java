package _blog.demo.model;

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
    @Column(nullable = false)
    private String password;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
}
