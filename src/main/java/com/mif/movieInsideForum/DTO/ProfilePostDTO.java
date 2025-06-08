package com.mif.movieInsideForum.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Field.VoteType;
import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.SimpleBadgeDTO;
import com.mif.movieInsideForum.Collection.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfilePostDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId groupId;
    private UserDTO owner;
    private String groupName;
    private String title;
    private String content;
    private List<String> mediaUrls;
    private Map<String, VoteType> userVotes = new HashMap<>();
    private Date createdAt;
    private Date updatedAt;
//    private SimpleBadgeDTO badge;
}
