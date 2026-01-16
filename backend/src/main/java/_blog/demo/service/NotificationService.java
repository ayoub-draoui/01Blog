package _blog.demo.service;

import _blog.demo.dto.NotificationResponse;
import _blog.demo.exceptions.ResourceNotFoundException;
import _blog.demo.model.*;
import _blog.demo.repository.NotificationRepository;
import _blog.demo.repository.PostRepository;
import _blog.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;

// import java.time.LocalDateTime;
import java.util.List;

@Service
// @AllArgsConstructor

public class NotificationService {

    private NotificationRepository notificationRepo;
    private UserRepository userRepo;
    private PostRepository postRepo;
    private SubscriptionService subscriptionService;


     public NotificationService(
            NotificationRepository notificationRepo,
            UserRepository userRepo,
            PostRepository postRepo,
            @Lazy SubscriptionService subscriptionService) {  
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.subscriptionService = subscriptionService;
    }


    // post notifications;
    public void CreatPostNotif(Long ahthorID, Long postID){
        Post post = postRepo.findById(postID).orElse(null);
        User author = userRepo.findById(ahthorID).orElse(null); 
        if (post == null || author == null) return;

        List<Subscription> followers = subscriptionService.getFollowers(ahthorID);
        String message = author.getUsername() + "posted" + post.getTitle();
        for (Subscription subscription : followers){
                if (!notificationRepo.existsByUserIdAndActorIdAndTypeAndRelatedPostId(
                    subscription.getFollowerId(),ahthorID, NotificationType.NEW_POST, postID
                )) {
                    Notification notif = new Notification();
                    notif.setUserId(subscription.getFollowerId());
                    notif.setActorId(ahthorID);
                    notif.setType(NotificationType.NEW_POST);
                    notif.setRelatedPostId(postID);
                    notif.setMessage(message);
                    notif.setIsRead(false);
                    notificationRepo.save(notif);
                }
            
            }
    }


            // create a new notification for for likes ;

        public void createNewFollowerNotification(Long followedUserId, Long followerId) {
        User follower = userRepo.findById(followerId).orElse(null);
        
        if (follower == null) return;

        String message = follower.getUsername() + " started following you";

        Notification notification = new Notification();
        notification.setUserId(followedUserId);
        notification.setActorId(followerId);
        notification.setType(NotificationType.NEW_FOLLOWER);
        notification.setMessage(message);
        notification.setIsRead(false);
        
        notificationRepo.save(notification);
    }


    // the same for the likes 
    
 public void createLikeNotification(Long postId, Long likerId) {
        Post post = postRepo.findById(postId).orElse(null);
        User liker = userRepo.findById(likerId).orElse(null);
        
        if (post == null || liker == null) return;
        
        // here user liking his own post, i think shold forbide his ass from liking his own post no suger coating;
        if (post.getAuthorId().equals(likerId)) return;

        String message = liker.getUsername() + " liked your post: " + post.getTitle();

        Notification notification = new Notification();
        notification.setUserId(post.getAuthorId());
        notification.setActorId(likerId);
        notification.setType(NotificationType.LIKE);
        notification.setRelatedPostId(postId);
        notification.setMessage(message);
        notification.setIsRead(false);
        
        notificationRepo.save(notification);
    }


    // this is for comments;

      public void createCommentNotification(Long postId, Long commentId, Long commenterId) {
        Post post = postRepo.findById(postId).orElse(null);
        User commenter = userRepo.findById(commenterId).orElse(null);
        
        if (post == null || commenter == null) return;
        
         
        if (post.getAuthorId().equals(commenterId)) return;

        String message = commenter.getUsername() + " commented on your post: " + post.getTitle();

        Notification notification = new Notification();
        notification.setUserId(post.getAuthorId());
        notification.setActorId(commenterId);
        notification.setType(NotificationType.COMMENT);
        notification.setRelatedPostId(postId);
        notification.setRelatedCommentId(commentId);
        notification.setMessage(message);
        notification.setIsRead(false);
        
        notificationRepo.save(notification);
    }


        // get all notifications by username;

    public Page<Notification> getUserNotifications(Long userId, int page, int size) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(
            userId, 
            PageRequest.of(page, size)
        );
    }


        // get fresh notif for user;

    public Page<Notification> getUnreadNotifications(Long userId, int page, int size) {
        return notificationRepo.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(
            userId, 
            PageRequest.of(page, size)
        );
    }

        // git l count for the front;

     public long getUnreadCount(Long userId) {
        return notificationRepo.countUnreadNotifications(userId);
    }



            // mark'em ad read when the client his the notif compo;


        public Notification markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepo.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
         
        if (!notification.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found");
        }

        notification.setIsRead(true);
        return notificationRepo.save(notification);
    }


        // mark all as read
     @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepo.markAllAsRead(userId);
    }

            // deelete notif;

     public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepo.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        // Ensure user owns this notification
        if (!notification.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found");
        }

        notificationRepo.delete(notification);
    }



    public NotificationResponse enrichNotification(Notification notif){
        User  actor = userRepo.findById(notif.getActorId()).orElse(null);
        Post relatedPostt =  notif.getRelatedPostId() != null 
        ?postRepo.findById(notif.getRelatedPostId()).orElse(null) 
        : null;
        return new  NotificationResponse(
            notif.getId(),
            notif.getUserId(),
            notif.getActorId(),
            actor != null? actor.getUsername() : "unknownUser",
            actor != null ? actor.getFirstname() : null,
            actor != null ? actor.getLastname() : null,
            actor != null ? actor.getAvatar() : null,
            notif.getType(),
            notif.getRelatedPostId(),
            relatedPostt != null ? relatedPostt.getTitle() : null,
            notif.getRelatedCommentId(),
            notif.getMessage(),
            notif.getIsRead(),
            notif.getCreatedAt()


        );




//         public record NotificationResponse(
//     Long id,
//     Long userId,
//     Long actorId,
//     String actorUsername,
//     String actorFirstname,
//     String actorLastname,
//     String actorAvatar,
//     NotificationType type,
//     Long relatedPostId,
//     String relatedPostTitle,
//     Long relatedCommentId,
//     String message,
//     Boolean isRead,
//     LocalDateTime createdAt
// ) {}
    }

    public Page<NotificationResponse> enrichNotifications(Page<Notification> notifications) {
        return notifications.map(this::enrichNotification);
    }



    
}
