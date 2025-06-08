package com.mif.movieInsideForum.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.UserBadgeDTO;
import com.mif.movieInsideForum.Module.ActivityAnalytics.enums.BadgeLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String displayName;
    private String profilePictureUrl;
    @Builder.Default
    private Map<String, BadgeLevel> badgeMap = new HashMap<>();
}