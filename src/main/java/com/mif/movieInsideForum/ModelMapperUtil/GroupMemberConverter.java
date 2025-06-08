package com.mif.movieInsideForum.ModelMapperUtil;

import com.mif.movieInsideForum.Collection.GroupMember;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.DTO.GroupMemberDTO;
import com.mif.movieInsideForum.Module.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupMemberConverter {
    private final UserRepository userRepository;

    public GroupMemberDTO convertToDTO(GroupMember groupMember) {
        GroupMemberDTO dto = new GroupMemberDTO();
        User user = userRepository.findById(groupMember.getUserId()).orElseThrow();
        dto.setAvatar(user.getProfilePictureUrl());
        dto.setDisplayName(user.getDisplayName());
        dto.setId(groupMember.getUserId());
        dto.setJoinedAt(groupMember.getJoinedAt());
        return dto;
    }

    public GroupMember convertToEntity(GroupMemberDTO groupMemberDTO) {
        GroupMember groupMember = new GroupMember();
        groupMember.setUserId(groupMemberDTO.getId());
        groupMember.setJoinedAt(groupMemberDTO.getJoinedAt());
        return groupMember;
    }
}