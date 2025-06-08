package com.mif.movieInsideForum.Module.Group;

import com.mif.movieInsideForum.Collection.Field.Status;
import com.mif.movieInsideForum.Collection.GroupRule;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.mif.movieInsideForum.DTO.GroupDTO;
import com.mif.movieInsideForum.DTO.GroupMemberDTO;
import com.mif.movieInsideForum.DTO.UserDTO;

import java.util.List;
import java.util.Map;

public interface GroupService {
    GroupDTO createGroup(GroupDTO request) throws Exception;

    GroupDTO updateGroup(ObjectId groupId, GroupDTO request) throws IllegalAccessException;

    Boolean addMemberToGroup(ObjectId groupId, ObjectId userId);

    Boolean removeMemberFromGroup(ObjectId groupId, ObjectId userId);

    void deleteGroup(ObjectId groupId);

    Boolean addPendingInvitation(ObjectId groupId, ObjectId userId);

    Boolean removePendingInvitation(ObjectId groupId, ObjectId userId);

    Boolean acceptInvitation(ObjectId groupId, ObjectId userId);

    GroupDTO findGroupById(ObjectId groupId);

    Boolean isUserOwnerOfGroup(ObjectId groupId, ObjectId userId);

    Slice<GroupDTO> findByOwnerId(ObjectId ownerId, Pageable pageable);

    Slice<GroupDTO> findByMemberId(ObjectId memberId, Pageable pageable);

    Slice<GroupDTO> findGroupUserNotJoin(ObjectId userId, Pageable pageable);

    Slice<GroupDTO> findByGroupNameContainingIgnoreCase(String groupName, Pageable pageable);

    Slice<GroupMemberDTO> getAllMembers(ObjectId groupId, Pageable pageable);

    Slice<UserDTO> getPendingInvitations(ObjectId groupId, Pageable pageable);

    Boolean rejectInvitation(ObjectId groupId, ObjectId userId);

    Map<String,Status> findUserStatusInGroups(ObjectId userId, List<ObjectId> groupIds);

    void addRuleToGroup(ObjectId groupId, GroupRule rule);
    void updateRuleInGroup(ObjectId groupId, ObjectId ruleId, GroupRule rule);
    void deleteRuleFromGroup(ObjectId groupId, ObjectId ruleId);
    List<GroupRule> getRulesByGroupId(ObjectId groupId);
    Slice<GroupMemberDTO> searchMembersByName(ObjectId groupId, String name, Pageable pageable);
    Boolean leaveGroup(ObjectId groupId, ObjectId userId);

    Page<GroupDTO> findAllGroups(Pageable pageable);

}