package com.mif.movieInsideForum.Module.Chat;

import com.mif.movieInsideForum.Collection.UserGroupChat;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupChatRepository extends MongoRepository<UserGroupChat, ObjectId> {

}
