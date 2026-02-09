package _blog.demo.dto;

public record UserProfileResponse(
    Long id,
    String username,
    String email,
    String firsname,
    String lastname,
    String avatar,
    String bio,
    String location,
    String website,
    String role,
    Boolean isBanned,
    Long followersCount,
    Long followingCount,
    Long postCount,
    Boolean isFollowing
) {
    
}
