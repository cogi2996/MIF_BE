package com.mif.movieInsideForum.Module.Notification;

import com.mif.movieInsideForum.Collection.Notification.Notification;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {
    Optional<Notification> findByGroupPostIdAndReceiverId(ObjectId groupPostId, ObjectId receiverId);

//    @Query(value ="{ 'groupPostId': ?0, 'senderId': ?1, 'type': { '$in': ['UP_VOTE', 'DOWN_VOTE'] } }", delete = true)
    @Query(value ="{ 'groupPostId': ?0, 'senderId': ?1, 'type': { '$in': ['UP_VOTE', 'DOWN_VOTE'] } }", delete = true)
    void deleteByGroupPostIdAndSenderIdAndTypeUpVoteOrDownVote(ObjectId groupPostId, ObjectId senderId);

    @Query(value = "{ 'groupId': ?0, 'senderId': ?1, 'type': 'JOIN_REQUEST' }",delete = true)
    void deleteByGroupIdAndSenderIdAndTypeJoinRequest(ObjectId groupId, ObjectId senderId);

    @Query(value = "{'receiverId': ?0}", sort = "{'createdAt': -1}")
    Slice<Notification> findAllWithPag(Pageable pageable, ObjectId receiverId);

    @Query(value = "{ 'receiverId': ?0, 'isRead': false }", count = true)
    long countUnreadNotifications(ObjectId receiverId);

}
