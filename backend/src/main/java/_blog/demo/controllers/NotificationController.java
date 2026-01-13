package _blog.demo.controllers;

import _blog.demo.dto.NotificationResponse;
import _blog.demo.model.Notification;
import _blog.demo.security.CustomUserDetails;
import _blog.demo.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
public class NotificationController {
    private NotificationService notifService;

    @GetMapping()
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Page<Notification> notifications = notifService.getUnreadNotifications(currentUser.getId(), page, size);
        Page<NotificationResponse> enriched = notifService.enrichNotifications(notifications);
        return ResponseEntity.ok(enriched);
    }

                // get unreaddd notif only ;
     @GetMapping("/unread")
    public ResponseEntity<Page<NotificationResponse>> getUnreadNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Page<Notification> notifications = notifService.getUnreadNotifications(
            currentUser.getId(), page, size
        );
        Page<NotificationResponse> enriched = notifService.enrichNotifications(notifications);
        
        return ResponseEntity.ok(enriched);
    }

        // bring l count ;

     @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        long count = notifService.getUnreadCount(currentUser.getId());
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

        // mark one notif as readddd ;

     @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Notification notification = notifService.markAsRead(notificationId, currentUser.getId());
        return ResponseEntity.ok(notification);
    }


            // make all readd;

       @PutMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        notifService.markAllAsRead(currentUser.getId());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "All notifications marked as read");
        return ResponseEntity.ok(response);
    }

//  delleeete  the notification;

        @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, String>> deleteNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        notifService.deleteNotification(notificationId, currentUser.getId());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification deleted");
        return ResponseEntity.ok(response);
    }

}
