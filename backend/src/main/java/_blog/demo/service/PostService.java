package _blog.demo.service;

import _blog.demo.dto.PostResponse;
import _blog.demo.dto.PostUpdateRequest;
import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.exceptions.UnauthorizedException;
import _blog.demo.model.Post;
import _blog.demo.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
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

   
    @Transactional(readOnly = true)
    public List<PostResponse> allPosts(Long currentUserId, int page, int size) {
        int offset = page * size;
        List<Object[]> results = postRepository.findAllPostsWithDetails(currentUserId, size, offset);

        return results.stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    
    @Transactional(readOnly = true)
    public long getTotalPostsCount() {
        return postRepository.countAllPosts();
    }
 
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId, Long currentUserId) {
        Object[] result = postRepository.findPostWithDetailsByIdAndUserId(postId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        return mapToPostResponse(result);
    }

    
    @Transactional(readOnly = true)
    public List<PostResponse> getByAuthor(Long authorId, Long currentUserId, int page, int size) {
        int offset = page * size;
        List<Object[]> results = postRepository.findPostsByAuthorWithDetails(authorId, currentUserId, size, offset);

        return results.stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }
 
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
            Object[] flatRow = flattenRow(row);
            return new PostResponse(
                convertToLong(flatRow[0]),              // id
                convertToString(flatRow[1]),            // title
                convertToString(flatRow[2]),            // content
                convertToLong(flatRow[3]),              // authorId
                convertToString(flatRow[9]),            // username
                convertToString(flatRow[10]),           // firstname
                convertToString(flatRow[11]),           // lastname
                convertToString(flatRow[12]),           // avatar
                convertToString(flatRow[5]),            // mediaUrl
                convertToString(flatRow[6]),            // mediaType
                convertToLong(flatRow[13]),             // likesCount
                convertToLong(flatRow[14]),             // commentsCount
                convertToBoolean(flatRow[15]),          // isLikedByCurrentUser
                convertToLocalDateTime(flatRow[7]),     // createdAt
                convertToLocalDateTime(flatRow[8])      // updatedAt
            );
       
    }
    private Long convertToLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof BigInteger) {
            return ((BigInteger) value).longValue();
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).longValue();
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    /**
     * Convert various boolean types
     */
    private Boolean convertToBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        if (value instanceof String) {
            String str = (String) value;
            return "true".equalsIgnoreCase(str) || "1".equals(str) || "t".equalsIgnoreCase(str);
        }
        return false;
    }


     private String convertToString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
     
        return value.toString();
    }
 
    private LocalDateTime convertToLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        }
        if (value instanceof java.util.Date) {
            return new java.sql.Timestamp(((java.util.Date) value).getTime()).toLocalDateTime();
        }
        throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to LocalDateTime");
    }
    private Object[] flattenRow(Object[] row) {
    if (row.length > 0 && row[0] instanceof Object[]) {
        return (Object[]) row[0];   
    }
    return row;
}
}