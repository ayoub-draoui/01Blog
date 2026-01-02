package _blog.demo.service;

import _blog.demo.dto.ChangePasswordRequest;
import _blog.demo.dto.UpdateProfileRequest;
import _blog.demo.dto.UserProfileResponse;
import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.exceptions.UnauthorizedException;
import _blog.demo.model.User;
import _blog.demo.repository.PostRepository;
import _blog.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepo;
    private PostRepository postRepo;
    private SubscriptionService subscriptionService;
    private FileStorageService fileStorageService;
    private PasswordEncoder passwordEncoder;

    public UserProfileResponse getUserProfile(Long userId, Long currentUserId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Get stats who follos whom 
        long followersCount = subscriptionService.getFollowersCount(userId);
        long followingCount = subscriptionService.getFollowingCount(userId);
        long postsCount = postRepo.countByAuthorId(userId);

        // Check if current user follows this user yla kont in my profile it shoulld 
        // give a null wla shi haja ;
        Boolean isFollowing = null;
        if (currentUserId != null && !currentUserId.equals(userId)) {
            isFollowing = subscriptionService.isFollowing(currentUserId, userId);
        }

        return new UserProfileResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstname(),
            user.getLastname(),
            user.getAvatar(),
            user.getBio(),
            user.getLocation(),
            user.getWebsite(),
            user.getRole().name(),
            followersCount,
            followingCount,
            postsCount,
            isFollowing
        );
    }
    public UserProfileResponse getUserProfileByUsername(String username, Long currentUserId) {
    User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

  
    long followersCount = subscriptionService.getFollowersCount(user.getId());
    long followingCount = subscriptionService.getFollowingCount(user.getId());
    long postsCount = postRepo.countByAuthorId(user.getId());

   
    Boolean isFollowing = null;
    if (currentUserId != null && !currentUserId.equals(user.getId())) {
        isFollowing = subscriptionService.isFollowing(currentUserId, user.getId());
    }

    return new UserProfileResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getFirstname(),
        user.getLastname(),
        user.getAvatar(),
        user.getBio(),
        user.getLocation(),
        user.getWebsite(),
        user.getRole().name(),
        followersCount,
        followingCount,
        postsCount,
        isFollowing
    );
}

    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.firstname() != null) {
            user.setFirstname(request.firstname());
        }
        if (request.lastname() != null) {
            user.setLastname(request.lastname());
        }
        if (request.bio() != null) {
            user.setBio(request.bio());
        }
        if (request.location() != null) {
            user.setLocation(request.location());
        }
        if (request.website() != null) {
            user.setWebsite(request.website());
        }

        return userRepo.save(user);
    }

    public User updateAvatar(Long userId, MultipartFile avatarFile) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Delete old avatar if exists
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            fileStorageService.deletFile(user.getAvatar());
        }

        // Upload new avatar
        String filename = fileStorageService.storeFile(avatarFile, "IMAGE");
        user.setAvatar(filename);

        return userRepo.save(user);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepo.save(user);
    }

    public User getUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
