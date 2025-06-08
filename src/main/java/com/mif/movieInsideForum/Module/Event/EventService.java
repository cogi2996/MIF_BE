package com.mif.movieInsideForum.Module.Event;

import com.mif.movieInsideForum.Collection.Event.Event;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.util.List;

public interface EventService {
    Event createEvent(Event event);
    void deleteEvent(ObjectId eventId);
    Event subscribeToEvent(ObjectId eventId, ObjectId userId);
    Event unsubscribeFromEvent(ObjectId eventId, ObjectId userId);
    Slice<Event> getSubscribedEvents(ObjectId userId, Pageable pageable);
    Slice<Event> getEventsByGroup(ObjectId groupId, Pageable pageable);
    List<Event> getUpcomingSubscribedEvents(ObjectId userId);
}