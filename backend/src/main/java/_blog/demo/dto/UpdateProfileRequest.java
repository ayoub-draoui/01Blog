package _blog.demo.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
     @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstname,
    
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastname,
    
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    String bio,
    
    @Size(max = 100, message = "Location cannot exceed 100 characters")
    String location,
    
    @Size(max = 200, message = "Website URL cannot exceed 200 characters")
    String website
) {
    
}
