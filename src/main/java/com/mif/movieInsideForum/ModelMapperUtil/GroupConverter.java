package com.mif.movieInsideForum.ModelMapperUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mif.movieInsideForum.DTO.GroupPostCount;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.mif.movieInsideForum.Collection.Group;
import com.mif.movieInsideForum.DTO.GroupDTO;
import com.mif.movieInsideForum.Module.Group.GroupRepository;
import com.mif.movieInsideForum.Module.Movie.MovieCategoryRepository;
import com.mif.movieInsideForum.Module.Post.service.GroupPostService;
import com.mif.movieInsideForum.Module.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GroupConverter {
    private final UserRepository userRepository;
    private final MovieCategoryRepository movieCategoryRepository;
    private final ModelMapper mapper;
    private final GroupRepository groupRepository;
    private final GroupPostService groupPostService; // Add this service

    public GroupDTO convertToDTO(Group group) {
        GroupDTO dto = mapper.map(group, GroupDTO.class);

        // Set member count
        dto.setMemberCount(group.getMembers().size());
        List<Group> groups = List.of(group);
//        List<Long> postCounts = groupPostService.getPostCountsForCurrentWeek(groups);
//        dto.setWeeklyPostCount(postCounts.isEmpty() ? 0L : postCounts.get(0));
        System.out.println("Weekly post count: " + dto.getWeeklyPostCount());
        return dto;
    }

    public Group convertToEntity(GroupDTO groupDTO) {
        return mapper.map(groupDTO, Group.class);
    }

    // GroupConverter.java
    public List<GroupDTO> convertToDTOList(List<Group> groups) {
        List<GroupPostCount> weeklyPostCounts = groupPostService.getPostCountsForCurrentWeek(groups);
        Map<ObjectId, GroupPostCount> weeklyPostCountsMap = weeklyPostCounts.stream()
                .collect(Collectors.toMap(GroupPostCount::getId, Function.identity()));
        return groups.stream()
                .map(group -> {
                    GroupDTO dto = convertToDTO(group);
                    GroupPostCount postCount = weeklyPostCountsMap.get(group.getId());
                    dto.setWeeklyPostCount(postCount != null ? postCount.getCount() : 0L);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}