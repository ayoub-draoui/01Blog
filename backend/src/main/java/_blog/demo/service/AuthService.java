package _blog.demo.service;

import _blog.demo.dto.LoginRequest;
import _blog.demo.dto.RegisterRequest;
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

    public String register(RegisterRequest req) {
    
        User user = new User();
        user.setUsername(req.username());
        user.setPassword(encoder.encode(req.password()));
        user.setRole(Role.ROLE_USER);
        userRepo.save(user);
        return "the user has been added successfully";
    }

    public String login(LoginRequest req) {
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(req.username(),
                                req.password()));
        User user = userRepo.findByUsername(req.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return jwtUtil.generateToken(userDetails);

    }

}
