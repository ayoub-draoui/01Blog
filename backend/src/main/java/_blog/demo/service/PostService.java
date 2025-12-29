package _blog.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import _blog.demo.dto.PostUpdateRequest;
import _blog.demo.model.Post;
import _blog.demo.repository.PostRepository;


@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public Post creatPost(Post post, Long authorId) {
        post.setAuthorId(authorId);
        return postRepository.save(post);
    }

    public Page<Post> allPosts(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size));
    }

    public Page<Post> getByAuthor(Long authorId, int page, int size) {
        return postRepository.findAllByAuthorId(authorId, PageRequest.of(page, size));
    }

    public Post updatePost(
            Long postId,
            Long currentUserId,
            PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getAuthorId().equals(currentUserId)) {
            throw new RuntimeException("You are not allowed to update this post");
        }

        post.setTitle(request.title());
        post.setContent(request.content());

        return postRepository.save(post);
    }

    public void delete(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthorId().equals(currentUserId)) {
            throw new RuntimeException("Not your post");
        }

        postRepository.delete(post);
    }
}
