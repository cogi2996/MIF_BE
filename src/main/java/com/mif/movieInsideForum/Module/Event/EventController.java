package com.mif.movieInsideForum.Module.Event;

import com.mif.movieInsideForum.Collection.Event.Event;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/events")
    public ResponseEntity<ResponseWrapper<Event>> createEvent(@RequestBody @Valid Event event, Principal principal) {
        event.setOwnerId(new ObjectId(principal.getName()));
        Event createdEvent = eventService.createEvent(event);
        return ResponseEntity.ok(new ResponseWrapper<>("success", "Event created successfully", createdEvent));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteEvent(@PathVariable ObjectId id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(new ResponseWrapper<>("success", "Event deleted successfully"));
    }
    
    @PostMapping("/events/{id}/subscribe")
    public ResponseEntity<ResponseWrapper<Event>> subscribeToEvent(@PathVariable ObjectId id, Principal principal) {
        Event event = eventService.subscribeToEvent(id, new ObjectId(principal.getName()));
        if (event != null) {
            return ResponseEntity.ok(new ResponseWrapper<>("success", "Subscribed to event successfully", event));
        } else {
            return ResponseEntity.status(404).body(new ResponseWrapper<>("error", "Event not found"));
        }
    }

    @PostMapping("/events/{id}/unsubscribe")
    public ResponseEntity<ResponseWrapper<Event>> unsubscribeFromEvent(@PathVariable ObjectId id, Principal principal) {
        Event event = eventService.unsubscribeFromEvent(id, new ObjectId(principal.getName()));
        if (event != null) {
            return ResponseEntity.ok(new ResponseWrapper<>("success", "Unsubscribed from event successfully", event));
        } else {
            return ResponseEntity.status(404).body(new ResponseWrapper<>("error", "Event not found"));
        }
    }

    @GetMapping("/subscribed-events")
    public ResponseEntity<ResponseWrapper<Slice<Event>>> getSubscribedEvents(Principal principal, @PageableDefault(size = 10) Pageable pageable) {
        Slice<Event> events = eventService.getSubscribedEvents(new ObjectId(principal.getName()), pageable);
        return ResponseEntity.ok(new ResponseWrapper<>("success", "Fetched subscribed events successfully", events));
    }

    @GetMapping("/groups/{groupId}/events")
    public ResponseEntity<ResponseWrapper<Slice<Event>>> getEventsByGroupId(@PathVariable ObjectId groupId, @PageableDefault(size = 8) Pageable pageable) {
        Slice<Event> events = eventService.getEventsByGroup(groupId, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<Event>>builder()
                .status("success")
                .message("Fetched events successfully")
                .data(events)
                .build());
    }
}