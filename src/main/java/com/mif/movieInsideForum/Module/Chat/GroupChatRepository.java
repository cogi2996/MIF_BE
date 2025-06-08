package com.mif.movieInsideForum.Module.Chat;

import com.mif.movieInsideForum.Collection.GroupChat;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupChatRepository extends MongoRepository<GroupChat, ObjectId> {
    // delete by groupId
    void deleteByGroupId(ObjectId groupId);
}
