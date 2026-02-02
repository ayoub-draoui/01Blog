package _blog.demo.repository;

import _blog.demo.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    long countByAuthorId(Long authorId);
    
    // Get single post with all data
    @Query(value = """
        SELECT 
            p.id,
            p.title,
            p.content,
            p.author_id,
            p.author_username,
            p.media_url,
            p.media_type,
            p.created_at,
            p.updated_at,
            u.username,
            u.firstname,
            u.lastname,
            u.avatar,
            COALESCE(COUNT(DISTINCT l.id), 0) as likes_count,
            COALESCE(COUNT(DISTINCT c.id), 0) as comments_count,
            CASE WHEN ul.id IS NOT NULL THEN true ELSE false END as is_liked
        FROM posts p
        INNER JOIN users u ON p.author_id = u.id
        LEFT JOIN likes l ON p.id = l.post_id
        LEFT JOIN comments c ON p.id = c.post_id
        LEFT JOIN likes ul ON p.id = ul.post_id AND ul.user_id = :currentUserId
        WHERE p.id = :postId
        GROUP BY p.id, p.title, p.content, p.author_id, p.author_username, 
                 p.media_url, p.media_type, p.created_at, p.updated_at,
                 u.username, u.firstname, u.lastname, u.avatar, ul.id
        """, nativeQuery = true)
    Optional<Object[]> findPostWithDetailsByIdAndUserId(
        @Param("postId") Long postId, 
        @Param("currentUserId") Long currentUserId
    );

    // Get all posts with all data (paginated version)
    @Query(value = """
        SELECT 
            p.id,
            p.title,
            p.content,
            p.author_id,
            p.author_username,
            p.media_url,
            p.media_type,
            p.created_at,
            p.updated_at,
            u.username,
            u.firstname,
            u.lastname,
            u.avatar,
            COALESCE(COUNT(DISTINCT l.id), 0) as likes_count,
            COALESCE(COUNT(DISTINCT c.id), 0) as comments_count,
            CASE WHEN ul.id IS NOT NULL THEN true ELSE false END as is_liked
        FROM posts p
        INNER JOIN users u ON p.author_id = u.id
        LEFT JOIN likes l ON p.id = l.post_id
        LEFT JOIN comments c ON p.id = c.post_id
        LEFT JOIN likes ul ON p.id = ul.post_id AND ul.user_id = :currentUserId
        GROUP BY p.id, p.title, p.content, p.author_id, p.author_username, 
                 p.media_url, p.media_type, p.created_at, p.updated_at,
                 u.username, u.firstname, u.lastname, u.avatar, ul.id
        ORDER BY p.created_at DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Object[]> findAllPostsWithDetails(
        @Param("currentUserId") Long currentUserId,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    // Count total posts (for pagination)
    @Query(value = "SELECT COUNT(*) FROM posts", nativeQuery = true)
    long countAllPosts();

    // Get posts by specific user
    @Query(value = """
        SELECT 
            p.id,
            p.title,
            p.content,
            p.author_id,
            p.author_username,
            p.media_url,
            p.media_type,
            p.created_at,
            p.updated_at,
            u.username,
            u.firstname,
            u.lastname,
            u.avatar,
            COALESCE(COUNT(DISTINCT l.id), 0) as likes_count,
            COALESCE(COUNT(DISTINCT c.id), 0) as comments_count,
            CASE WHEN ul.id IS NOT NULL THEN true ELSE false END as is_liked
        FROM posts p
        INNER JOIN users u ON p.author_id = u.id
        LEFT JOIN likes l ON p.id = l.post_id
        LEFT JOIN comments c ON p.id = c.post_id
        LEFT JOIN likes ul ON p.id = ul.post_id AND ul.user_id = :currentUserId
        WHERE p.author_id = :authorId
        GROUP BY p.id, p.title, p.content, p.author_id, p.author_username, 
                 p.media_url, p.media_type, p.created_at, p.updated_at,
                 u.username, u.firstname, u.lastname, u.avatar, ul.id
        ORDER BY p.created_at DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Object[]> findPostsByAuthorWithDetails(
        @Param("authorId") Long authorId,
        @Param("currentUserId") Long currentUserId,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    // Get personalized feed (posts from users that currentUser follows)
    @Query(value = """
        SELECT 
            p.id,
            p.title,
            p.content,
            p.author_id,
            p.author_username,
            p.media_url,
            p.media_type,
            p.created_at,
            p.updated_at,
            u.username,
            u.firstname,
            u.lastname,
            u.avatar,
            COALESCE(COUNT(DISTINCT l.id), 0) as likes_count,
            COALESCE(COUNT(DISTINCT c.id), 0) as comments_count,
            CASE WHEN ul.id IS NOT NULL THEN true ELSE false END as is_liked
        FROM posts p
        INNER JOIN users u ON p.author_id = u.id
        INNER JOIN subscriptions s ON p.author_id = s.following_id AND s.follower_id = :currentUserId
        LEFT JOIN likes l ON p.id = l.post_id
        LEFT JOIN comments c ON p.id = c.post_id
        LEFT JOIN likes ul ON p.id = ul.post_id AND ul.user_id = :currentUserId
        GROUP BY p.id, p.title, p.content, p.author_id, p.author_username, 
                 p.media_url, p.media_type, p.created_at, p.updated_at,
                 u.username, u.firstname, u.lastname, u.avatar, ul.id
        ORDER BY p.created_at DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Object[]> findPersonalizedFeedWithDetails(
        @Param("currentUserId") Long currentUserId,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    // Count posts in personalized feed
    @Query(value = """
        SELECT COUNT(DISTINCT p.id)
        FROM posts p
        INNER JOIN subscriptions s ON p.author_id = s.following_id 
        WHERE s.follower_id = :currentUserId
        """, nativeQuery = true)
    long countPersonalizedFeed(@Param("currentUserId") Long currentUserId);
}