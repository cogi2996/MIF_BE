package com.mif.movieInsideForum.Module.Chat;

import com.mif.movieInsideForum.Collection.GroupChat;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface UserGroupChatService {
    void joinGroupChat(ObjectId userId, ObjectId groupId);
    Slice<GroupChat> getGroupChats(ObjectId userId, Pageable pageable);
}
