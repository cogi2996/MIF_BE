package com.mif.movieInsideForum.ModelMapperUtil;

import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.DTO.ProfilePostDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfilePostConverter {
    private final ModelMapper mapper;

    public ProfilePostDTO ConvertToDTO(GroupPost groupPost) {
        ProfilePostDTO profilePostDTO = mapper.map(groupPost, ProfilePostDTO.class);
        profilePostDTO.setGroupId(groupPost.getGroup().getId());
        profilePostDTO.setGroupName(groupPost.getGroup().getGroupName());
        return profilePostDTO;
    }
}