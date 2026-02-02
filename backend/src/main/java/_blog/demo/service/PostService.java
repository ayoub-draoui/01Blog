package _blog.demo.service;

import _blog.demo.dto.PostResponse;
import _blog.demo.dto.PostUpdateRequest;
import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.exceptions.UnauthorizedException;
import _blog.demo.model.Post;
import _blog.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final LikeService likeService;
    private final CommentService commentService;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    public PostService(PostRepository postRepository,
            LikeService likeService,
            CommentService commentService,
            FileStorageService fileStorageService,
            NotificationService notificationService) {
        this.postRepository = postRepository;
        this.likeService = likeService;
        this.commentService = commentService;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
    }

    
    @Transactional
    public Post creatPost(Post post, String username, Long authorId, MultipartFile mediaFile) {
        post.setAuthorId(authorId);
        post.setAuthorUsername(username);

        if (mediaFile != null && !mediaFile.isEmpty()) {
            String contentType = mediaFile.getContentType();
            String mediaType = null;

            if (contentType != null) {
                if (contentType.startsWith("image/")) {
                    mediaType = "IMAGE";
                } else if (contentType.startsWith("video/")) {
                    mediaType = "VIDEO";
                }
            }

            if (mediaType != null) {
                String filename = fileStorageService.storeFile(mediaFile, mediaType);
                post.setMediaUrl(filename);
                post.setMediaType(mediaType);
            }
        }

        Post savedPost = postRepository.save(post);
        notificationService.CreatPostNotif(authorId, savedPost.getId());

        return savedPost;
    }

    /**
     * Get all posts with pagination - OPTIMIZED SINGLE QUERY
     * Returns PostResponse with all data (author info, likes count, comments count, is liked)
     */
    @Transactional(readOnly = true)
    public List<PostResponse> allPosts(Long currentUserId, int page, int size) {
        int offset = page * size;
        List<Object[]> results = postRepository.findAllPostsWithDetails(currentUserId, size, offset);
        
        return results.stream()
            .map(this::mapToPostResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get total count of posts for pagination
     */
    @Transactional(readOnly = true)
    public long getTotalPostsCount() {
        return postRepository.countAllPosts();
    }

    /**
     * Get single post by ID - OPTIMIZED SINGLE QUERY
     * Returns PostResponse with all data
     */
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId, Long currentUserId) {
        Object[] result = postRepository.findPostWithDetailsByIdAndUserId(postId, currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        return mapToPostResponse(result);
    }

    /**
     * Get posts by author - OPTIMIZED SINGLE QUERY
     * Returns PostResponse with all data
     */
    @Transactional(readOnly = true)
    public List<PostResponse> getByAuthor(Long authorId, Long currentUserId, int page, int size) {
        int offset = page * size;
        List<Object[]> results = postRepository.findPostsByAuthorWithDetails(authorId, currentUserId, size, offset);
        
        return results.stream()
            .map(this::mapToPostResponse)
            .collect(Collectors.toList());
    }

    /**
     * Update an existing post
     */
    @Transactional
    public Post updatePost(
            Long postId,
            Long currentUserId,
            PostUpdateRequest request, 
            MultipartFile mediaFile) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        if (!post.getAuthorId().equals(currentUserId)) {
            throw new UnauthorizedException("You are not allowed to update this post");
        }

        post.setTitle(request.title());
        post.setContent(request.content());
        
        if (mediaFile != null && !mediaFile.isEmpty()) {
            // Delete old media if exists
            if (post.getMediaUrl() != null) {
                fileStorageService.deletFile(post.getMediaUrl());
            }

            String contentType = mediaFile.getContentType();
            String mediaType = null;

            if (contentType != null) {
                if (contentType.startsWith("image/")) {
                    mediaType = "IMAGE";
                } else if (contentType.startsWith("video/")) {
                    mediaType = "VIDEO";
                }
            }

            if (mediaType != null) {
                String filename = fileStorageService.storeFile(mediaFile, mediaType);
                post.setMediaUrl(filename);
                post.setMediaType(mediaType);
            }
        }

        return postRepository.save(post);
    }

    /**
     * Get simple post by ID (without enrichment) - used internally
     */
    @Transactional(readOnly = true)
    public Post getPostEntityById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    /**
     * Delete a post
     */
    @Transactional
    public void delete(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (!post.getAuthorId().equals(currentUserId)) {
            throw new UnauthorizedException("Not your post, you can't delete it");
        }
        
        // Delete associated media file if exists
        if (post.getMediaUrl() != null) {
            fileStorageService.deletFile(post.getMediaUrl());
        }

        // Delete associated likes and comments
        likeService.deleteAllLikesForPost(postId);
        commentService.deleteAllCommentsForPost(postId);
        
        postRepository.delete(post);
    }

    /**
     * Helper method to map Object[] from native query to PostResponse
     * This is the CRITICAL mapping function!
     * PUBLIC so other services (like FeedService) can use it
     */
    public PostResponse mapToPostResponse(Object[] row) {
        return new PostResponse(
            ((Number) row[0]).longValue(),                              // id
            (String) row[1],                                            // title
            (String) row[2],                                            // content
            ((Number) row[3]).longValue(),                              // authorId
            (String) row[9],                                            // username
            (String) row[10],                                           // firstname
            (String) row[11],                                           // lastname
            (String) row[12],                                           // avatar
            (String) row[5],                                            // mediaUrl
            (String) row[6],                                            // mediaType
            ((Number) row[13]).longValue(),                             // likesCount
            ((Number) row[14]).longValue(),                             // commentsCount
            (Boolean) row[15],                                          // isLikedByCurrentUser
            ((java.sql.Timestamp) row[7]).toLocalDateTime(),           // createdAt
            ((java.sql.Timestamp) row[8]).toLocalDateTime()            // updatedAt
        );
    }
}