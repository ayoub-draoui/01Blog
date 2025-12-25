package _blog.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import _blog.demo.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
