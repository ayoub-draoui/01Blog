package _blog.demo.controllers;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import _blog.demo.model.Post;
import _blog.demo.service.PostService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    public PostController(PostService postService){
        this.postService =  postService;
    }
    @PostMapping
    public Post create(@ModelAttribute Post post){
        System.out.println("create post");
        return postService.creatPost(post);
    }
    @GetMapping("/{id}")
    public Post getOne(@PathVariable Long id) {
        return postService.findById(id);
    }
    @GetMapping
    public List<Post> getThemAll(){
        return postService.allPosts();
    }
    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @RequestBody Post post) {        
        return postService.update(id, post);
    }
}
