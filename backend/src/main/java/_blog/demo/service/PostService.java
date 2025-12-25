package _blog.demo.service;
import java.util.List;
import org.springframework.stereotype.Service;
import _blog.demo.model.Post;
import _blog.demo.repository.PostRepository;

@Service
public class PostService {
    private final PostRepository postRepository;
    public PostService(PostRepository postRepository){
        this.postRepository = postRepository;
    }
    public Post creatPost(Post post){
        return postRepository.save(post);
    }

    public List<Post> allPosts(){
        return postRepository.findAll();
    }
    public Post findById(Long id){
            return postRepository.findById(id).orElseThrow(()-> 

            new RuntimeException("there is no post yet for this user "));
    }
    public Post update(Long id , Post updatposte){
            Post post = findById(id);
            post.setContent(updatposte.getContent());
            post.setTitle(updatposte.getTitle());
            return postRepository.save(post);
    }
    public void deletPost(Long id ){
        postRepository.deleteById(id);
    }
}
