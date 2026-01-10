package _blog.demo.service;

import _blog.demo.dto.AuthResponse;
import _blog.demo.dto.LoginRequest;
import _blog.demo.dto.RegisterRequest;
import _blog.demo.exceptions.UserAlreadyExistsException;
import _blog.demo.model.*;
import _blog.demo.repository.UserRepository;
import _blog.demo.security.JwtUtil;
import lombok.AllArgsConstructor;
import _blog.demo.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private UserRepository userRepo;
    private PasswordEncoder encoder;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.findByUsername(req.username()).isPresent()) {
            throw new UserAlreadyExistsException("Username '" + req.username() + "' is already taken");
        }
         if (userRepo.findByEmail(req.email()).isPresent()) {
            throw new UserAlreadyExistsException("Email '" + req.email() + "' is already registered");
        }

        User user = new User();
        user.setUsername(req.username());
        user.setEmail(req.email());
        user.setFirstname(req.firstname());
        user.setLastname(req.lastname());
        user.setAvatar("");  
        user.setPassword(encoder.encode(req.password()));
        user.setRole(Role.ROLE_USER);
        userRepo.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);
        return new AuthResponse(
                token,
                user.getId(),
                 user.getUsername(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getAvatar(),
                user.getRole().name(),
                "the user has been added successfully");
    }

    public AuthResponse login(LoginRequest req) {
        System.out.println(req.usernameOrEmail());
        System.out.println(req.password());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.usernameOrEmail(), req.password()));

          User user = userRepo.findByUsernameOrEmail(req.usernameOrEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);
        return new AuthResponse(
                token,
              user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getAvatar(),
                user.getRole().name(),
                "Login successsfull"
            );

    }

//     public record AuthResponse(
//    String token,
//     Long userId,
//     String username,
//     String email,
//     String firstname,
//     String lastname,
//     String avatar,
//     String role,
//     String message
// ) {}

}
