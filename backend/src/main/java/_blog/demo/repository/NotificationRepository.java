package _blog.demo.repository;
import _blog.demo.model.Notification;
import _blog.demo.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    // git all the notif b tartiib ;
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    //  this is goona brinng just lii ba9i mata9raw ;
    Page<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

        // count the unreaad notif to show in the front;

        @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.isRead = false")
    long countUnreadNotifications(@Param("userId") Long userId);
    

        // thiis wiill mark'em ad readd when the user hit the notif componene
     @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") Long userId);

}
