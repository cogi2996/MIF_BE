package com.mif.movieInsideForum.Mapper;

import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.DTO.UserDTO;
import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.UserBadgeDTO;
import com.mif.movieInsideForum.Module.ActivityAnalytics.enums.BadgeLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "badgeMap", source = "badgeMap")
    UserDTO toUserDTO(User user);

    default UserDTO toUserDTOWithGroupBadge(User user, String groupId) {
        UserDTO userDTO = toUserDTO(user);
        if (userDTO.getBadgeMap() != null && userDTO.getBadgeMap().containsKey(groupId)) {
            // Nếu cần, có thể filter chỉ lấy huy hiệu của group cụ thể
            Map<String, BadgeLevel> groupBadgeMap = new HashMap<>();
            groupBadgeMap.put(groupId, userDTO.getBadgeMap().get(groupId));
            userDTO.setBadgeMap(groupBadgeMap);
        }
        return userDTO;
    }
} 