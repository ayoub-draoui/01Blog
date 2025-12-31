package _blog.demo.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
            @NotBlank(message = "Username is required") @Size(min = 3, max = 30, message = "User name should be between 3 and 50 char ") 
            String username,
            @NotBlank(message = "Email is required") @Email(message = "You should respect the format blog@example.com") 
            String email,
            @NotBlank(message = "Firstname is required") @Size(min = 3, max = 30, message = "Firstname should be between 3 and 50 char ") 
            String firstname,
            @NotBlank(message = "Lastname is required") @Size(min = 3, max = 30, message = "Lasttname should be between 3 and 50 char ")
            String lastname,
            @NotBlank(message = "Password is required") @Size(min = 3, max = 30, message = "Password should be between 3 and 50 char ") 
            String password
      ) 
{}