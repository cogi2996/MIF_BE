package com.mif.movieInsideForum.Module.Group;

import com.mif.movieInsideForum.Collection.Field.Status;
import com.mif.movieInsideForum.Collection.GroupRule;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostResponseDTO;
import com.mif.movieInsideForum.Module.Post.service.GroupPostService;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.mif.movieInsideForum.DTO.*;
import com.mif.movieInsideForum.Security.AuthenticationFacade;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final AuthenticationFacade authenticationFacade;
    private final GroupPostService groupPostService;

    @DeleteMapping("/groups/{groupId}/members")
    public ResponseEntity<ResponseWrapper<Void>> leaveGroup(@PathVariable ObjectId groupId) {
        ObjectId userId = authenticationFacade.getUserId();
        Boolean isLeft = groupService.leaveGroup(groupId, userId);
        if (!isLeft) {
            return ResponseEntity.badRequest().body(ResponseWrapper.<Void>builder().status("error").message("Không thể rời nhóm").build());
        }
        return ResponseEntity.ok(ResponseWrapper.<Void>builder().status("success").message("Bạn đã rời nhóm thành công").build());
    }

    @GetMapping("/groups/{groupId}/members")
    public ResponseEntity<ResponseWrapper<Slice<GroupMemberDTO>>> getAllMembers(
            @PathVariable ObjectId groupId,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 5) Pageable pageable) {
        Slice<GroupMemberDTO> members;
        if (name != null && !name.isEmpty()) {
            log.info("searching members by name: " + name);
            members = groupService.searchMembersByName(groupId, name, pageable);
        } else {
            members = groupService.getAllMembers(groupId, pageable);
        }
        return ResponseEntity.ok(ResponseWrapper.<Slice<GroupMemberDTO>>builder()
                .status("success")
                .data(members)
                .message("Danh sách thành viên nhóm")
                .build());
    }

    @PostMapping("/groups")
    public ResponseEntity<ResponseWrapper<GroupDTO>> createGroup(@RequestBody GroupDTO groupDTO) throws Exception {
        log.info("usergroup: " + authenticationFacade.getUser());
        UserDTO ownerDTO = UserDTO.builder().id(authenticationFacade.getUser().getId()).displayName(authenticationFacade.getUser().getDisplayName()).profilePictureUrl(authenticationFacade.getUser().getProfilePictureUrl()).build();
        groupDTO.setOwner(ownerDTO);
        GroupDTO createdGroup = groupService.createGroup(groupDTO);
        return ResponseEntity.ok(ResponseWrapper.<GroupDTO>builder().status("success").data(createdGroup).message("Nhóm đã được tạo thành công").build());
    }

    @PostMapping("/groups/{groupId}/members/{userId}")
    public ResponseEntity<ResponseWrapper<Void>> addMemberToGroup(@PathVariable ObjectId groupId, @PathVariable ObjectId userId) {
        Boolean isAdded = groupService.addMemberToGroup(groupId, userId);
        if (!isAdded) {
            return ResponseEntity.badRequest().body(ResponseWrapper.<Void>builder().status("error").message("Không thể thêm thành viên vào nhóm").build());
        }
        return ResponseEntity.ok(ResponseWrapper.<Void>builder().status("success").message("Thành viên đã được thêm vào nhóm").build());
    }

    @DeleteMapping("/groups/{groupId}/members/{userId}")
    public ResponseEntity<ResponseWrapper<Void>> removeMemberFromGroup(@PathVariable ObjectId groupId, @PathVariable ObjectId userId) {
        Boolean isRemoved = groupService.removeMemberFromGroup(groupId, userId);
        if (!isRemoved) {
            return ResponseEntity.badRequest().body(ResponseWrapper.<Void>builder().status("error").message("Không thể xóa thành viên khỏi nhóm").build());
        }
        return ResponseEntity.ok(ResponseWrapper.<Void>builder().status("success").message("Thành viên đã được xóa khỏi nhóm").build());
    }

    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteGroup(@PathVariable ObjectId groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder().status("success").message("Nhóm đã được xóa").build());
    }

    @PatchMapping("/groups/{groupId}")
    public ResponseEntity<ResponseWrapper<GroupDTO>> updateGroup(@PathVariable ObjectId groupId, @RequestBody GroupDTO request) throws IllegalAccessException {
        GroupDTO updatedGroup = groupService.updateGroup(groupId, request);
        return ResponseEntity.ok(ResponseWrapper.<GroupDTO>builder().status("success").data(updatedGroup).message("Nhóm đã được cập nhật").build());
    }

    @PostMapping("/groups/{groupId}/pending-invitations")
    public ResponseEntity<ResponseWrapper<Void>> addPendingInvitation(@PathVariable ObjectId groupId, Principal principal) {
        Boolean isAdded = groupService.addPendingInvitation(groupId, new ObjectId(principal.getName()));
        if (!isAdded) {
            return ResponseEntity.badRequest().body(ResponseWrapper.<Void>builder().status("error").message("Không thể thêm lời mời").build());
        }
        return ResponseEntity.ok(ResponseWrapper.<Void>builder().status("success").message("Lời mời đã được thêm").build());
    }

    @DeleteMapping("/groups/{groupId}/pending-invitations/{userId}")
    public ResponseEntity<ResponseWrapper<Void>> removePendingInvitation(@PathVariable ObjectId groupId, @PathVariable ObjectId userId) {
        Boolean isRemoved = groupService.removePendingInvitation(groupId, userId);
        if (!isRemoved) {
            return ResponseEntity.badRequest().body(ResponseWrapper.<Void>builder().status("error").message("Không thể xóa lời mời").build());
        }
        return ResponseEntity.ok(ResponseWrapper.<Void>builder().status("success").message("Lời mời đã được xóa").build());
    }

    @PostMapping("/groups/{groupId}/accept-invitations/{userId}")
    public ResponseEntity<ResponseWrapper<Void>> acceptInvitation(@PathVariable ObjectId groupId, @PathVariable ObjectId userId) {
        Boolean isAccepted = groupService.acceptInvitation(groupId, userId);
        if (!isAccepted) {
            return ResponseEntity.badRequest().body(ResponseWrapper.<Void>builder().status("error").message("Không thể chấp nhận lời mời").build());
        }
        return ResponseEntity.ok(ResponseWrapper.<Void>builder().status("success").message("Lời mời đã được chấp nhận").build());
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<ResponseWrapper<GroupDTO>> findGroupById(@PathVariable ObjectId groupId) {
        GroupDTO group = groupService.findGroupById(groupId);
        return ResponseEntity.ok(ResponseWrapper.<GroupDTO>builder().status("success").data(group).message("Thông tin nhóm").build());
    }

    //    @GetMapping("/groups/{groupId}/members")
//    public ResponseEntity<ResponseWrapper<Slice<GroupMemberDTO>>> getAllMembers(@PathVariable ObjectId groupId, @PageableDefault(size = 5) Pageable pageable) {
//        Slice<GroupMemberDTO> members = groupService.getAllMembers(groupId, pageable);
//        return ResponseEntity.ok(ResponseWrapper.<Slice<GroupMemberDTO>>builder().status("success").data(members).message("Danh sách thành viên nhóm").build());
//    }


    @GetMapping("/user/{memberId}/groups")
    public ResponseEntity<ResponseWrapper<Slice<GroupDTO>>> getUserGroups(@PathVariable ObjectId memberId, @PageableDefault(size = 5) Pageable pageable) {
        Slice<GroupDTO> groups = groupService.findByMemberId(memberId, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<GroupDTO>>builder().status("success").data(groups).message("Danh sách nhóm của người dùng").build());
    }

    @GetMapping("/explore-groups")
    public ResponseEntity<ResponseWrapper<Slice<GroupDTO>>> findGroupUserNotJoin(@PageableDefault(size = 5) Pageable pageable) {
        ObjectId userId = authenticationFacade.getUser().getId();
        Slice<GroupDTO> groups = groupService.findGroupUserNotJoin(userId, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<GroupDTO>>builder().status("success").data(groups).message("Danh sách nhóm chưa tham gia").build());
    }

    @GetMapping("/groups/search")
    public ResponseEntity<ResponseWrapper<Slice<GroupDTO>>> searchGroupByGroupName(@RequestParam String name, @PageableDefault(size = 5) Pageable pageable) {
        Slice<GroupDTO> groups = groupService.findByGroupNameContainingIgnoreCase(name, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<GroupDTO>>builder().status("success").data(groups).message("Kết quả tìm kiếm nhóm").build());
    }

    @GetMapping("/my-groups")
    public ResponseEntity<ResponseWrapper<Slice<GroupDTO>>> findByOwnerId(@PageableDefault(size = 12) Pageable pageable) {
        ObjectId ownerId = authenticationFacade.getUser().getId();
        Slice<GroupDTO> groups = groupService.findByOwnerId(ownerId, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<GroupDTO>>builder().status("success").data(groups).message("Danh sách nhóm của bạn").build());
    }

    @GetMapping("/groups/{groupId}/posts")
    public ResponseEntity<ResponseWrapper<Slice<GroupPostResponseDTO>>> getPostsByGroupId(
            @PathVariable ObjectId groupId, 
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        
        // Kiểm tra nếu người dùng hiện tại là group owner
        boolean isGroupOwner = groupService.isUserOwnerOfGroup(groupId, new ObjectId(authentication.getName()));
        
        // Nếu là group owner thì truyền true, không thì false
        boolean includeBlocked = isGroupOwner;
        
        Slice<GroupPostResponseDTO> posts = groupPostService.getPostsByGroupId(groupId, pageable, includeBlocked);
        return ResponseEntity.ok(ResponseWrapper.<Slice<GroupPostResponseDTO>>builder()
                .status("success")
                .data(posts)
                .message("Danh sách bài đăng trong nhóm")
                .build());
    }

    @GetMapping("/groups/{groupId}/pending-invitations")
    public ResponseEntity<ResponseWrapper<Slice<UserDTO>>> getPendingInvitations(@PathVariable ObjectId groupId, @PageableDefault(size = 10) Pageable pageable) {
        Slice<UserDTO> pendingInvitations = groupService.getPendingInvitations(groupId, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<UserDTO>>builder().status("success").data(pendingInvitations).message("Danh sách lời mời đang chờ").build());
    }

    @DeleteMapping("/groups/{groupId}/pending-invitation")
    public ResponseEntity<ResponseWrapper<Void>> rejectInvitation(@PathVariable ObjectId groupId, @RequestBody Map<String, ObjectId> request) {
        ObjectId userId = request.get("userId");
        Boolean isRejected = groupService.rejectInvitation(groupId, userId);
        if (!isRejected) {
            return ResponseEntity.badRequest().body(ResponseWrapper.<Void>builder().status("error").message("Không thể từ chối lời mời").build());
        }
        return ResponseEntity.ok(ResponseWrapper.<Void>builder().status("success").message("Lời mời đã được từ chối").build());
    }

    @PostMapping("/groups/batch-status")
    public ResponseEntity<ResponseWrapper<Map<String, Status>>> getUserStatusInGroups(@RequestBody UserGroupStatusDTO userGroupStatusDTO) {
        ObjectId userId = authenticationFacade.getUser().getId();
        Map<String, Status> statuses = groupService.findUserStatusInGroups(userId, userGroupStatusDTO.getGroupIds());
        return ResponseEntity.ok(ResponseWrapper.<Map<String, Status>>builder().status("success").message("Trạng thái của người dùng trong các nhóm").data(statuses).build());
    }

    @PostMapping("/groups/{groupId}/rules")
    public ResponseEntity<ResponseWrapper<Void>> addRuleToGroup(@PathVariable ObjectId groupId, @RequestBody GroupRule rule) {
        groupService.addRuleToGroup(groupId, rule);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder().status("success").message("Rule added successfully").build());
    }

    @PutMapping("/groups/{groupId}/rules/{ruleId}")
    public ResponseEntity<ResponseWrapper<Void>> updateRuleInGroup(@PathVariable ObjectId groupId, @PathVariable ObjectId ruleId, @RequestBody GroupRule rule) {
        groupService.updateRuleInGroup(groupId, ruleId, rule);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder().status("success").message("Rule updated successfully").build());
    }

    @DeleteMapping("/groups/{groupId}/rules/{ruleId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteRuleFromGroup(@PathVariable ObjectId groupId, @PathVariable ObjectId ruleId) {
        groupService.deleteRuleFromGroup(groupId, ruleId);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder().status("success").message("Rule deleted successfully").build());
    }

    @GetMapping("/groups/{groupId}/rules")
    public ResponseEntity<ResponseWrapper<List<GroupRule>>> getRulesByGroupId(@PathVariable ObjectId groupId) {
        List<GroupRule> rules = groupService.getRulesByGroupId(groupId);
        return ResponseEntity.ok(ResponseWrapper.<List<GroupRule>>builder().status("success").data(rules).message("Rules retrieved successfully").build());
    }

    @GetMapping("/groups")
    public ResponseEntity<ResponseWrapper<Page<GroupDTO>>> findAllGroupsAsPage(
            @PageableDefault(size = 5) Pageable pageable) {
        Page<GroupDTO> groups = groupService.findAllGroups(pageable);
        return ResponseEntity.ok(ResponseWrapper.<Page<GroupDTO>>builder().status("success").data(groups).message("Danh sách nhóm").build());

    }
}