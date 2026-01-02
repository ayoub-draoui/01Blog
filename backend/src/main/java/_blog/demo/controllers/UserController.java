package _blog.demo.controllers;

import _blog.demo.dto.ChangePasswordRequest;
import _blog.demo.dto.UpdateProfileRequest;
import _blog.demo.dto.UserProfileResponse;
import _blog.demo.model.User;
import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;



@RestController
@RequestMapping("/users")
@AllArgsConstructor

public class UserController {
    private UserService userService;


    // thatss gonna bring the users profile;
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(
        @AuthenticationPrincipal CustomUserDetails currentUser
    ) {

         UserProfileResponse profile = userService.getUserProfile(currentUser.getId(), currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    // this is gonna bring any profile i want bl ID dyaloo;
      @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        UserProfileResponse profile = userService.getUserProfile(userId, currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    // bring the MF by his username
        @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfileByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        UserProfileResponse profile = userService.getUserProfileByUsername(username, currentUser.getId());
        return ResponseEntity.ok(profile);
    }


        // thiss gonna update the PF;
      @PutMapping("/me")
    public ResponseEntity<User> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        User updatedUser = userService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(updatedUser);
    }


    // this gonna change the password ;

      @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        userService.changePassword(currentUser.getId(), request);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return ResponseEntity.ok(response);
    }

    
}
