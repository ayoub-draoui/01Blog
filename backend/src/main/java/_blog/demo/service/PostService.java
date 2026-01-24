package _blog.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import _blog.demo.dto.PostUpdateRequest;
import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.exceptions.UnauthorizedException;
import _blog.demo.model.Post;
import _blog.demo.repository.PostRepository;

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

    public Page<Post> allPosts(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size));
    }

    public Page<Post> getByAuthor(Long authorId, int page, int size) {
        return postRepository.findAllByAuthorId(authorId, PageRequest.of(page, size));
    }

    public Post updatePost(
            Long postId,
            Long currentUserId,
            PostUpdateRequest request, MultipartFile mediaFile) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        if (!post.getAuthorId().equals(currentUserId)) {
            throw new UnauthorizedException("You are not allowed to update this post");
        }

        post.setTitle(request.title());
        post.setContent(request.content());
        if (mediaFile != null && !mediaFile.isEmpty()) {
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

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    public void delete(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with this post is :" + postId));

        if (!post.getAuthorId().equals(currentUserId)) {
            throw new UnauthorizedException("Not your post  u can't delet it go away");
        }
        if (post.getMediaUrl() != null) {
            fileStorageService.deletFile(post.getMediaUrl());
        }

        likeService.deleteAllLikesForPost(postId);
        commentService.deleteAllCommentsForPost(postId);
        postRepository.delete(post);
    }
}
