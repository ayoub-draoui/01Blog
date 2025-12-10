package _blog.demo.controllers;

import _blog.demo.dto.LoginRequest;
import _blog.demo.dto.RegisterRequest;
import _blog.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 

@RestController
@RequestMapping("/auth")
public class AuthController {
@Autowired
private AuthService authService;
@PostMapping("/register")
public String register(@RequestBody RegisterRequest req){
    return authService.register(req);
}
@PostMapping("/login")
public String login(@RequestBody LoginRequest req){
    return authService.login(req);
}
}
