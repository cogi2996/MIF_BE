package com.mif.movieInsideForum.Module.Notification;

import com.mif.movieInsideForum.Collection.Notification.Notification;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
//@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<ResponseWrapper<Slice<Notification>>> getAllWithPaging(@PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable, Principal principal) {
        log.info("Lấy thông báo cho người dùng: " + principal.getName());
        Slice<Notification> notifications = notificationService.getAllWithPaging(pageable, new ObjectId(principal.getName()));
        return ResponseEntity.ok(ResponseWrapper.<Slice<Notification>>builder()
                .status("success")
                .message("Lấy thông báo thành công")
                .data(notifications)
                .build());
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<ResponseWrapper<Notification>> markAsRead(@PathVariable ObjectId id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(ResponseWrapper.<Notification>builder()
                .status("success")
                .message("Đánh dấu thông báo đã đọc thành công")
                .data(notification)
                .build());
    }


    @GetMapping("/notifications/unread-count")
    public ResponseEntity<ResponseWrapper<Long>> getUnreadNotificationCount(Principal principal) {
        long unreadCount = notificationService.countUnreadNotifications(new ObjectId(principal.getName()));
        return ResponseEntity.ok(new ResponseWrapper<>("success", "Fetched unread notification count successfully", unreadCount));
    }
}