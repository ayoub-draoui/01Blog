package _blog.demo.dto;

public record AuthResponse(
   String token,
    Long userId,
    String username,
    String email,
    String firstname,
    String lastname,
    String avatar,
    String role,
    String message
) {}
