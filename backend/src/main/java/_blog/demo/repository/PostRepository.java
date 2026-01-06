package _blog.demo.repository;
import _blog.demo.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface PostRepository extends JpaRepository<Post, Long>{
    Page<Post> findAllByAuthorId(Long authorId, Pageable pageable);
    long countByAuthorId(Long authorId);
            // this is gonna bring the posts for the feeed , is gonna bring onlyy the posts
    @Query("SELECT p FROM Post p WHERE p.authorId IN :followingIds ORDER BY p.createdAt DESC")
    Page<Post> findPostsByFollowingUsers(@Param("followingIds") List<Long> followingIds, Pageable pageable);
    


//  this is a joined query need to be useeed in future if givees better 
// and fast navigation should be implemented later khaso shwiiya dyal jahd (; ;

    // @Query("SELECT p FROM Post p JOIN Subscription s ON p.authorId = s.followingId " +
    //        "WHERE s.followerId = :userId ORDER BY p.createdAt DESC")
    // Page<Post> findFeedForUser(@Param("userId") Long userId, Pageable pageable);

}
