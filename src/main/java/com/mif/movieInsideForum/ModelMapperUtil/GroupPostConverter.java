package com.mif.movieInsideForum.ModelMapperUtil;

import com.mif.movieInsideForum.Collection.Field.VoteType;
import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostRequestDTO;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostResponseDTO;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import com.mif.movieInsideForum.Module.Post.service.GroupPostRatingsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupPostConverter {

    private final ModelMapper mapper;
    private final GroupPostRatingsService groupPostRatingsService;
    private final AuthenticationFacade authenticationFacade;

    public GroupPostResponseDTO convertToDTO(GroupPost groupPost) {
        GroupPost x = groupPost;
        GroupPostResponseDTO dto = mapper.map(groupPost, GroupPostResponseDTO.class);
        System.out.println("GroupPostConverter: convertToDTO: groupPost: " + groupPost.getGroup());
        dto.setGroupId(groupPost.getGroup().getId());

        // Calculate voteNumber and userVotes
        int voteNumber =groupPost.getRatingCount();
        VoteType userVotes = groupPostRatingsService.getUserVote(groupPost.getId(), authenticationFacade.getUser().getId());

        dto.setVoteNumber(voteNumber);
        dto.setUserVotes(userVotes);

        return dto;
    }

    public GroupPost convertToEntity(GroupPostRequestDTO groupPostRequestDTO) {
        return mapper.map(groupPostRequestDTO, GroupPost.class);
    }
}