package com.mif.movieInsideForum.Module.Group;

import com.mif.movieInsideForum.Collection.*;
import com.mif.movieInsideForum.Collection.Notification.NotificationType;
import com.mif.movieInsideForum.Collection.Field.Status;
import com.mif.movieInsideForum.Collection.Notification.Notification;
import com.mif.movieInsideForum.DTO.GroupDTO;
import com.mif.movieInsideForum.DTO.GroupMemberDTO;
import com.mif.movieInsideForum.DTO.GroupStatus;
import com.mif.movieInsideForum.DTO.UserDTO;
import com.mif.movieInsideForum.Mapper.UserMapper;
import com.mif.movieInsideForum.Messaging.Producer.NotificationProducer;
import com.mif.movieInsideForum.ModelMapperUtil.GroupConverter;
import com.mif.movieInsideForum.ModelMapperUtil.GroupMemberConverter;
import com.mif.movieInsideForum.Module.Chat.GroupChatRepository;
import com.mif.movieInsideForum.Module.Comment.CommentRepository;
import com.mif.movieInsideForum.Module.Event.EventRepository;
import com.mif.movieInsideForum.Module.Movie.MovieCategoryRepository;
import com.mif.movieInsideForum.Module.Post.repository.GroupPostRatingsRepository;
import com.mif.movieInsideForum.Module.Post.repository.SavedPostRepository;
import com.mif.movieInsideForum.Module.Post.repository.GroupPostRepository;
import com.mif.movieInsideForum.Module.User.UserRepository;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import com.mif.movieInsideForum.Util.Patcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final MovieCategoryRepository movieCategoryRepository;
    private final GroupConverter groupConverter;
    private final GroupMemberConverter groupMemberConverter;
    private final AuthenticationFacade authenticationFacade;
    private final NotificationProducer notificationProducer;
    private final GroupPostRepository groupPostRepository;
    private final GroupChatRepository groupChatRepository;
    private final SavedPostRepository savedPostRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final GroupPostRatingsRepository groupPostRatingsRepository;


    @Override
    @Transactional
    public GroupDTO createGroup(GroupDTO request) {
        User owner = userRepository.findById(request.getOwner().getId()).orElseThrow();
        MovieCategory category = movieCategoryRepository.findById(request.getCategoryId()).orElseThrow();
        Group group = new Group();
        group.setGroupName(request.getGroupName());
        group.setDescription(request.getDescription());
        group.setOwner(owner);
        group.setCategory(category);
        group.getMembers().add(new GroupMember(owner.getId(), new Date()));
        group.setGroupType(request.getGroupType());
        group.setIsPublic(request.getIsPublic());
        groupRepository.save(group);
        Group savedGroup = groupRepository.findById(group.getId()).orElseThrow();
        // save group chat
        groupChatRepository.save(GroupChat.builder()
                .groupId(savedGroup.getId())
                .groupName(savedGroup.getGroupName())
                .newestMessage("")
                .updateTime(new Date())
                .build());
        return groupConverter.convertToDTO(savedGroup);
    }

    @Override
    public GroupDTO updateGroup(ObjectId groupId, GroupDTO groupDTO) throws IllegalAccessException {
        Group existingGroup = groupRepository.findById(groupId).orElseThrow();
        Patcher.groupPatcher(existingGroup, groupDTO);
        Group savedGroup = groupRepository.save(existingGroup);
        return groupConverter.convertToDTO(savedGroup);
    }

    @Override
    public Boolean addMemberToGroup(ObjectId groupId, ObjectId userId) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        group.getMembers().add(new GroupMember(userId, new Date()));
        return groupRepository.save(group) != null;
    }

    @Override
    public Boolean removeMemberFromGroup(ObjectId groupId, ObjectId userId) {
        // Check if the group exists
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if the current user is the owner of the group
        ObjectId currentUserId = authenticationFacade.getUser().getId();
        if (!group.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("User is not the owner of this group");
        }

        // Check if the member is part of the group
        boolean memberExists = group.getMembers().stream().anyMatch(member -> member.getUserId().equals(userId));
        if (!memberExists) {
            throw new RuntimeException("Member does not belong to this group");
        }

        // Proceed to remove the member
        group.getMembers().removeIf(member -> member.getUserId().equals(userId));
        if (groupRepository.save(group) == null) {
            return false;

        } else {
            return true;
        }

    }

    @Override
    @Transactional
    public void deleteGroup(ObjectId groupId) {
        ObjectId currentUserId = new ObjectId(authenticationFacade.getAuthentication().getName());
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));

        // Validate that the user is the owner of the group
        if (!group.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("User is not the owner of the group");
        }
        try {
            // Delete group's posts
            groupPostRepository.deleteByGroupId(groupId);
            // delete post's rating by groupId
            groupPostRatingsRepository.deleteByGroupId(groupId);
            // delete post's comment
            commentRepository.deleteByGroupId(groupId);
            // Delete the group
            groupRepository.delete(group);
            // Delete saved posts
            savedPostRepository.deleteByGroupId(groupId);
            // delete event by groupId
            eventRepository.deleteByGroupId(groupId);
            // delete groupchat by groupId
            groupChatRepository.deleteByGroupId(groupId);
        } catch (Exception e) {
            log.error("Error deleting group with id {}: {}", groupId, e.getMessage());
            throw new RuntimeException("Failed to delete group");
        }
    }

    @Override
    public Boolean addPendingInvitation(ObjectId groupId, ObjectId userId) {
        // Tìm nhóm và người dùng
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // user la admin cua group thi khong the add
        if (group.getOwner().getId().equals(userId)) {
            throw new RuntimeException("User is the owner of the group");
        }

        // Kiểm tra nếu người dùng đã có trong danh sách pendingInvitations
        if (groupRepository.existsByIdAndPendingInvitations_Id(groupId, userId)) {
            throw new RuntimeException("User already has a pending invitation");
        }

        // Thêm lời mời vào danh sách pendingInvitations
        group.getPendingInvitations().add(user);
        ObjectId adminGroup = group.getOwner().getId();

        // Tạo thông báo yêu cầu tham gia nhóm
        Notification notification = Notification.builder()
                .groupId(groupId) // Thiết lập nhóm
                .groupName(group.getGroupName()) // Thiết lập tên nhóm
                .receiverId(adminGroup) // Thiết lập ID người nhận
                .groupAvatar(group.getAvatarUrl()) // Thiết lập ảnh nhóm
                .senderId(userId) // Thiết lập ID người gửi
                .message("Một người đã gửi gửi yêu cầu tham gia nhóm của bạn") // Nội dung thông báo
                .type(NotificationType.JOIN_REQUEST) // Thiết lập loại thông báo
                .build();

        notificationProducer.sendNotification(notification);
        // Gọi phương thức xử lý thông báo

        // Lưu nhóm và trả về kết quả
        return groupRepository.save(group) != null;
    }


    @Override
    public Boolean removePendingInvitation(ObjectId groupId, ObjectId userId) {

        if (!groupRepository.existsByCanUserRemovePendingInvitation(groupId, userId)) {
            throw new RuntimeException("User cannot remove pending invitation");
        }
        Group group = groupRepository.findById(groupId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        ObjectId ownerGroupId = group.getOwner().getId();
        ObjectId currentUserId = new ObjectId(authenticationFacade.getAuthentication().getName());
        // Check if the user is the owner of the group
        if (ownerGroupId.equals(userId) || !(ownerGroupId.equals(currentUserId) || userId.equals(currentUserId))) {
            throw new RuntimeException("User is not allowed to remove this pending invitation");
        }


        boolean removed = group.getPendingInvitations().remove(user);

        notificationProducer.sendNotification(Notification
                .builder()
                .groupId(groupId)
                .senderId(userId)
                .isRemove(true)
                .type(NotificationType.JOIN_REQUEST)
                .build());
        groupRepository.save(group);
        return removed;
    }

    @Override
    public Boolean acceptInvitation(ObjectId groupId, ObjectId userId) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (group.getPendingInvitations().remove(user)) {
            group.getMembers().add(new GroupMember(userId, new Date()));
        }
        return groupRepository.save(group) != null;
    }

    @Override
    public GroupDTO findGroupById(ObjectId groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        return groupConverter.convertToDTO(group);
    }

    @Override
    public Boolean isUserOwnerOfGroup(ObjectId groupId, ObjectId userId) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        return group.getOwner().getId().equals(userId);
    }

    @Override
    public Slice<GroupDTO> findByOwnerId(ObjectId ownerId, Pageable pageable) {
        Slice<Group> groups = groupRepository.findByOwnerId(ownerId, pageable);
        List<GroupDTO> groupDTOs = groupConverter.convertToDTOList(groups.getContent());
        return new SliceImpl<>(groupDTOs, pageable, groups.hasNext());
    }

    @Override
    public Slice<GroupDTO> findByMemberId(ObjectId memberId, Pageable pageable) {
        Slice<Group> groups = groupRepository.findByMemberId(memberId, pageable);
        List<GroupDTO> groupDTOs = groupConverter.convertToDTOList(groups.getContent());
        return new SliceImpl<>(groupDTOs, pageable, groups.hasNext());
    }

    @Override
    public Slice<GroupDTO> findGroupUserNotJoin(ObjectId userId, Pageable pageable) {
        Slice<Group> groups = groupRepository.findGroupUserNotJoin(userId, pageable);
        List<GroupDTO> groupDTOs = groupConverter.convertToDTOList(groups.getContent());
        return new SliceImpl<>(groupDTOs, pageable, groups.hasNext());
    }

    @Override
    public Slice<GroupDTO> findByGroupNameContainingIgnoreCase(String groupName, Pageable pageable) {
        Slice<Group> groups = groupRepository.findByGroupNameContainingIgnoreCase(groupName, pageable);
        List<GroupDTO> groupDTOs = groupConverter.convertToDTOList(groups.getContent());
        return new SliceImpl<>(groupDTOs, pageable, groups.hasNext());
    }

    @Override
    public Slice<GroupMemberDTO> getAllMembers(ObjectId groupId, Pageable pageable) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        List<GroupMember> members = group.getMembers();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), members.size());
        List<GroupMemberDTO> memberDTOs = members.subList(start, end).stream()
                .map(groupMemberConverter::convertToDTO)
                .collect(Collectors.toList());
        return new SliceImpl<>(memberDTOs, pageable, members.size() > end);
    }

    @Override
    public Slice<UserDTO> getPendingInvitations(ObjectId groupId, Pageable pageable) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        List<User> pendingInvitations = group.getPendingInvitations();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), pendingInvitations.size());
        List<UserDTO> userDTOs = pendingInvitations.subList(start, end).stream()
                .map(user -> UserMapper.INSTANCE.toUserDTOWithGroupBadge(user, groupId.toString()))
                .collect(Collectors.toList());
        return new SliceImpl<>(userDTOs, pageable, pendingInvitations.size() > end);
    }

    @Override
    public Boolean rejectInvitation(ObjectId groupId, ObjectId userId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        boolean removed = group.getPendingInvitations().remove(user);
        if (removed) {
            groupRepository.save(group);
        }
        return removed;
    }

    @Override
    public Map<String, Status> findUserStatusInGroups(ObjectId userId, List<ObjectId> groupIds) {
        System.out.println("userId: " + userId.toHexString());
        List<GroupStatus> status = groupRepository.findUserStatusInGroups(userId, groupIds);
        Map<String, Status> result = new HashMap<>();
        groupIds.forEach(id -> {
            result.put(id.toHexString(), Status.NOT_JOIN);
        });
        status.forEach(s -> {
            result.put(s.getGroupId().toHexString(), s.getStatus());
        });
        return result;
    }

    // src/main/java/com/mif/movieInsideForum/Service/GroupServiceImpl.java
    @Override
    public void addRuleToGroup(ObjectId groupId, GroupRule rule) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            group.getRuleList().add(rule);
            groupRepository.save(group);
        } else {
            throw new RuntimeException("Group not found");
        }
    }

    @Override
    public void updateRuleInGroup(ObjectId groupId, ObjectId ruleId, GroupRule rule) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            List<GroupRule> rules = group.getRuleList();
            for (int i = 0; i < rules.size(); i++) {
                if (rules.get(i).getId().equals(ruleId)) {
                    rules.set(i, rule);
                    rule.setId(ruleId);
                    groupRepository.save(group);
                    return;
                }
            }
            throw new RuntimeException("Rule not found");
        } else {
            throw new RuntimeException("Group not found");
        }
    }

    @Override
    public void deleteRuleFromGroup(ObjectId groupId, ObjectId ruleId) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            group.getRuleList().removeIf(rule -> rule.getId().equals(ruleId));
            groupRepository.save(group);
        } else {
            throw new RuntimeException("Group not found");
        }
    }

    @Override
    public List<GroupRule> getRulesByGroupId(ObjectId groupId) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isPresent()) {
            return optionalGroup.get().getRuleList();
        }
        throw new RuntimeException("Group not found");
    }

    @Override
    public Slice<GroupMemberDTO> searchMembersByName(ObjectId groupId, String name, Pageable pageable) {
        Slice<User> users = userRepository.findByNameAndGroupId(name, groupId, pageable);
        log.info("users_slice: " + users.isEmpty());
        List<GroupMemberDTO> memberDTOs = users.stream()
                .map(user -> groupMemberConverter.convertToDTO(new GroupMember(user.getId(), new Date())))
                .toList();
        return new SliceImpl<>(memberDTOs, pageable, users.hasNext());
    }

    @Override
    public Boolean leaveGroup(ObjectId groupId, ObjectId userId) {
        // Kiểm tra xem người dùng có phải là thành viên của nhóm không
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.getMembers().stream().anyMatch(member -> member.getUserId().equals(userId))) {
            throw new RuntimeException("User is not a member of the group");
        }

        // Xóa người dùng khỏi danh sách thành viên
        group.getMembers().removeIf(member -> member.getUserId().equals(userId));
        groupRepository.save(group);
        return true;
    }

    @Override
    public Page<GroupDTO> findAllGroups(Pageable pageable) {
        return groupRepository.findAll(pageable).map(groupConverter::convertToDTO);
    }

}