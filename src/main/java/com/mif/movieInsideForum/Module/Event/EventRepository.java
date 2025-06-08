package com.mif.movieInsideForum.Module.Event;

import com.mif.movieInsideForum.Collection.Event.Event;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, ObjectId> {

    @Query("{ 'userJoin': ?0 }")
    Slice<Event> findSubscribedEvents(ObjectId userId, Pageable pageable);
    @Query("{ 'groupId': ?0 }")
    Slice<Event> findByGroupId(ObjectId groupId, Pageable pageable); // New query method

    @Query(value = "{ 'userJoin': ?0, 'startDate': { $gt: ?1 } }", sort = "{ 'startDate': 1 }")
    List<Event> findUpcomingSubscribedEvents(ObjectId userId, Date currentDate);

    // delete by groupId
    void deleteByGroupId(ObjectId groupId);
}