package com.mif.movieInsideForum.Mapper;

import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.DTO.ProfilePostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfilePostMapper {
    // to profilePostDTO
    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.groupName", target = "groupName")
    ProfilePostDTO toProfilePostDTO(GroupPost groupPost);
    // to profilePostDetailDTO
}
