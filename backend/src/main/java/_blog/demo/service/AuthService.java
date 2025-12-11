package _blog.demo.service;

import _blog.demo.dto.LoginRequest;
import _blog.demo.dto.RegisterRequest;
import _blog.demo.model.*;
import _blog.demo.repository.UserRepository;
import _blog.demo.security.JwtUtil;
import _blog.demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    public String register(RegisterRequest req) {
    //     if (userRepo.findByUsername(req.getUsername()) != null) {
    //         // throw new RuntimeException("user name already exist555555555=====");
    //     }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        userRepo.save(user);
        return "the user has been added successfully";
    }

    public String login(LoginRequest req) {
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(req.getUsername(),
                                req.getPassword()));
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return jwtUtil.generateToken(userDetails.getUsername());

    }

}
