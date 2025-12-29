package _blog.demo.repository;
import _blog.demo.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface PostRepository extends JpaRepository<Post, Long>{
    Page<Post> findAllByAuthorId(Long authorId, Pageable pageable);
}
