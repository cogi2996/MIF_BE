package com.mif.movieInsideForum.Module.Chat;

import com.mif.movieInsideForum.Collection.GroupChat;
import com.mif.movieInsideForum.Collection.UserGroupChat;
import com.mif.movieInsideForum.Module.Group.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserGroupChatServiceImpl implements UserGroupChatService {
    private final UserGroupChatRepository userGroupChatRepository;
    private final GroupChatRepository groupChatRepository;
    private final GroupRepository groupRepository;

    @Override
    public void joinGroupChat(ObjectId userId, ObjectId groupId) {
        Optional<GroupChat> groupChatOptional = groupChatRepository.findById(groupId);
        if (groupChatOptional.isEmpty()) {
            throw new RuntimeException("GroupChat not found");
        }

        GroupChat groupChat = groupChatOptional.get();
        // Get the group's avatar from the Group collection
        String groupAvatar = groupRepository.findById(groupId)
                .map(group -> group.getAvatarUrl())
                .orElse("https://mif-bucket-1.s3.ap-southeast-1.amazonaws.com/9eee504b-9d04-4fae-acec-32d81f520a53_defaul_background_group.png");
        
        groupChat.setAvatarUrl(groupAvatar);
        groupChat = groupChatRepository.save(groupChat);

        UserGroupChat userGroupChat = userGroupChatRepository.findById(userId).orElse(new UserGroupChat(userId, new HashMap<>()));
        userGroupChat.getGroupChats().put(groupId, groupChat);
        userGroupChatRepository.save(userGroupChat);
    }

    @Override
    public Slice<GroupChat> getGroupChats(ObjectId userId, Pageable pageable) {
        UserGroupChat userGroupChat = userGroupChatRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("UserGroupChat not found"));

        List<GroupChat> sortedGroupChats = userGroupChat.getSortedGroupChats();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedGroupChats.size());
        List<GroupChat> pageContent = sortedGroupChats.subList(start, end);

        return new SliceImpl<>(pageContent, pageable, end < sortedGroupChats.size());
    }
}