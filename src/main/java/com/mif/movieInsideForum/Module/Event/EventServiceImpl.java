package com.mif.movieInsideForum.Module.Event;

import com.mif.movieInsideForum.Collection.Event.Event;
import com.mif.movieInsideForum.Collection.Event.EventType;
import com.mif.movieInsideForum.Collection.Event.SocialType;
import com.mif.movieInsideForum.Collection.Group;
import com.mif.movieInsideForum.Collection.GroupMember;
import com.mif.movieInsideForum.Collection.Notification.Notification;
import com.mif.movieInsideForum.Collection.Notification.NotificationType;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.DTO.message.EventMessage;
import com.mif.movieInsideForum.Exception.EventNotFoundException;
import com.mif.movieInsideForum.Exception.UserAlreadySubscribedException;
import com.mif.movieInsideForum.Exception.UserNotSubscribedException;
import com.mif.movieInsideForum.Module.Group.GroupRepository;
import com.mif.movieInsideForum.Module.User.UserRepository;
import com.mif.movieInsideForum.Module.Notification.QuartzSchedulerService;
import com.mif.movieInsideForum.Module.ActivityAnalytics.activity.ActivityMessageService;
import com.mif.movieInsideForum.Property.EventProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final QuartzSchedulerService quartzSchedulerService;
    private final GroupRepository groupRepository;
    private final EventProperties eventProperties;
    private final ActivityMessageService activityMessageService;

    @Override
    public Slice<Event> getEventsByGroup(ObjectId groupId, Pageable pageable) {
        return eventRepository.findByGroupId(groupId, pageable);
    }

    @Override
    public Event createEvent(Event event) {
        event.setLink(null);
        // type offline
        if (event.getEventType() == EventType.OFFLINE) {
            if (event.getLocation() == null) {
                throw new RuntimeException("Location is required for offline event.");
            }
            event.setSocialType(null);
        }

        // type online
        if (event.getEventType() == EventType.ONLINE) {
            if (event.getSocialType() == SocialType.OTHER && event.getLink() == null) {
                throw new RuntimeException("Link is required for other social type.");
            }
            if(event.getSocialType() == SocialType.MIF_LIVE){
                int roomID = (int) (Math.random() * 9000) + 1000; // Random number between 1000 and 9999
                String liveLink = eventProperties.getFrontendHost() + "/vi/live?roomID=" + roomID;
                event.setLink(liveLink);
            }
            event.setLocation(null);
        }

        // validate group_id
        Optional<Group> groupOtp = groupRepository.findById(event.getGroupId());
        if (groupOtp.isEmpty()) {
            throw new RuntimeException("Group not found.");
        }
        Group group = groupOtp.get();
        // user must in group which create event
        List<ObjectId> groupMemberIds = group.getMembers().stream().map(GroupMember::getUserId).toList();
        if (!groupMemberIds.contains(event.getOwnerId())) {
            throw new RuntimeException("User must be in group to create event.");
        }

//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MINUTE, 2); // Thêm 3 phút
//        event.setStartDate(calendar.getTime());
        log.info("Event: {}", event);
        Event dbEvent = eventRepository.save(event);
        subscribeToEvent(dbEvent.getId(), event.getOwnerId());
        return dbEvent;
    }

    @Override
    public void deleteEvent(ObjectId eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public Event subscribeToEvent(ObjectId eventId, ObjectId userId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<User> receiver = userRepository.findById(userId);

        if (eventOptional.isEmpty()) {
            throw new EventNotFoundException("Event not found.");
        }

        Event event = eventOptional.get();
        // Kiểm tra role cho MIF_LIVE
        if (event.getSocialType() == SocialType.MIF_LIVE && !event.getOwnerId().equals(userId)) {
            event.setLink(event.getLink() + "&role=Audience");
        }

        if (event.getUserJoin().contains(userId)) {
            throw new UserAlreadySubscribedException("User is already subscribed to this event.");
        }

        if (receiver.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        // Thêm user vào danh sách tham gia sự kiện
        event.getUserJoin().add(userId);
        eventRepository.save(event);

        // Send activity message for event join
        activityMessageService.sendGroupEventJoined(
            userId.toString(),
            eventId.toString(),
            event.getGroupId().toString()
        );

        // Lấy thời gian hiện tại và thời gian bắt đầu sự kiện
        Date now = new Date();
        Date startDate = event.getStartDate();
        
        // Tính thời gian còn lại đến khi sự kiện bắt đầu (tính bằng phút)
        long timeUntilEventInMinutes = (startDate.getTime() - now.getTime()) / (60 * 1000);
        
        // Xác định thời gian gửi thông báo
        Date scheduleTime;
        String notificationMessage;
        
        if (timeUntilEventInMinutes <= 0) {
            // Sự kiện đã bắt đầu
            notificationMessage = "Sự kiện " + event.getEventName() + " đã bắt đầu!";
            scheduleTime = now; // Gửi ngay lập tức
        } else if (timeUntilEventInMinutes < eventProperties.getNotificationMinutesBefore()) {
            // Sự kiện sẽ diễn ra trong vòng ít hơn thời gian thông báo mặc định
            notificationMessage = "Sự kiện " + event.getEventName() + " sẽ bắt đầu trong " + timeUntilEventInMinutes + " phút nữa!";
            scheduleTime = now; // Gửi ngay lập tức
        } else {
            // Trường hợp bình thường - thông báo trước thời gian mặc định
            notificationMessage = "Sự kiện " + event.getEventName() + " sẽ bắt đầu trong " + eventProperties.getNotificationMinutesBefore() + " phút. Hãy chuẩn bị!";
            scheduleTime = new Date(startDate.getTime() - eventProperties.getNotificationMinutesBefore() * 60 * 1000);
        }

        // Tạo message để gửi
        String subscriberEmail = receiver.get().getEmail();
        EventMessage eventMessage = EventMessage.builder()
                .eventId(eventId)
                .subscriberId(userId)
                .subscriberEmail(subscriberEmail)
                .subject("Thông báo sự kiện")
                .content(notificationMessage + "\n" + event.getLink())
                .build();

        Notification notification = Notification.builder()
                .message(notificationMessage)
                .receiverId(userId)
                .eventId(eventId)
                .url(event.getLink())
                .groupId(event.getGroupId())
                .type(NotificationType.EVENT)
                .build();

        // Lên lịch gửi email
        quartzSchedulerService.scheduleEmailJob(eventMessage, notification, scheduleTime);
        return event;
    }

    @Override
    @Transactional
    public Event unsubscribeFromEvent(ObjectId eventId, ObjectId userId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new EventNotFoundException("Event not found.");
        }

        Event event = eventOptional.get();
        if (!event.getUserJoin().contains(userId)) {
            throw new UserNotSubscribedException("User is not subscribed to this event.");
        }

        // Xóa user khỏi danh sách tham gia sự kiện
        event.getUserJoin().remove(userId);
        // Xóa job đã lên lịch qua QuartzSchedulerService
        quartzSchedulerService.deleteScheduledJob(eventId, userId);

        return eventRepository.save(event);
    }

    @Override
    public Slice<Event> getSubscribedEvents(ObjectId userId, Pageable pageable) {
        return eventRepository.findSubscribedEvents(userId, pageable);
    }

    @Override
    public List<Event> getUpcomingSubscribedEvents(ObjectId userId) {
        Date currentDate = new Date();
        List<Event> events = eventRepository.findUpcomingSubscribedEvents(userId, currentDate);
        return events.stream()
                .limit(5)
                .toList();
    }
}