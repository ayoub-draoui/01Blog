package _blog.demo.controllers;

import _blog.demo.model.Post;
import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.FeedService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
@AllArgsConstructor

public class FeedController {
    
}
