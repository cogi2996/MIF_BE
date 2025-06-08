package com.mif.movieInsideForum.DTO.Response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Field.VoteType;
import com.mif.movieInsideForum.DTO.UserDTO;
import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.SimpleBadgeDTO;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewestPostResponseDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId groupId;
    private String groupName;
    private Boolean isJoined;
    private UserDTO owner;
    private String title;
    private String content;
    private List<String> mediaUrls;  // Optional list of media URLs (images, videos, etc.)
    @Builder.Default
    private int voteNumber = 0;
    private VoteType userVotes;
    private Date createdAt;
    private Date updatedAt;
//    private SimpleBadgeDTO badge;
}



