package com.mif.movieInsideForum.Mapper;

import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostRequestDTO;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostResponseDTO;
import com.mif.movieInsideForum.DTO.Response.NewestPostResponseDTO;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import com.mif.movieInsideForum.Module.Post.service.GroupPostRatingsService;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class GroupPostMapper {

    @Autowired
    private GroupPostRatingsService groupPostRatingsService;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    protected GroupPostRatingsService getGroupPostRatingsService() {
        return groupPostRatingsService;
    }

    protected AuthenticationFacade getAuthenticationFacade() {
        return authenticationFacade;
    }

    @Mapping(source = "groupId", target = "group.id")
    public abstract GroupPost convertToEntity(GroupPostRequestDTO groupPostRequestDTO);

//    @Mapping(source = "groupId", target = "groupId")
    @Mapping(source = "group.groupName", target = "groupName")
    @Mapping(source = "ratingCount", target = "voteNumber")
    @Mapping(source = "group.id", target = "groupId")
    @Mapping(target = "userVotes", expression = "java(getGroupPostRatingsService().getUserVote(groupPost.getId(), getAuthenticationFacade().getUser().getId()))")
    @Mapping(source = "owner.badgeMap", target = "owner.badgeMap")
    public abstract NewestPostResponseDTO toNewestPostResponseDTO(GroupPost groupPost);

    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "ratingCount", target = "voteNumber")
    @Mapping(target = "userVotes", expression = "java(getGroupPostRatingsService().getUserVote(groupPost.getId(), getAuthenticationFacade().getUser().getId()))")
    @Mapping(source = "owner.badgeMap", target = "owner.badgeMap")
    public abstract GroupPostResponseDTO toGroupPostResponseDTO(GroupPost groupPost);

//    @AfterMapping
//    protected void afterMappingToGroupPostResponseDTO(GroupPost groupPost, @MappingTarget GroupPostResponseDTO dto) {
//        int voteNumber = groupPost.getRatingCount();
//        VoteType userVotes = groupPostRatingsService.getUserVote(groupPost.getId(), authenticationFacade.getUser().getId());
//        dto.setVoteNumber(voteNumber);
//        dto.setUserVotes(userVotes);
//    }

//    @AfterMapping
//    protected void afterMappingToGroupPostResponseDTO(GroupPost groupPost, @MappingTarget NewestPostResponseDTO dto) {
//        VoteType userVotes = groupPostRatingsService.getUserVote(groupPost.getId(), authenticationFacade.getUser().getId());
//        dto.setUserVotes(userVotes);
//    }
}