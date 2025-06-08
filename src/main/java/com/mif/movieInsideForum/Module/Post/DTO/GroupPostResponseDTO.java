package com.mif.movieInsideForum.Module.Post.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Field.VoteType;
import com.mif.movieInsideForum.DTO.UserDTO;
import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.SimpleBadgeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPostResponseDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId groupId;
    private UserDTO owner;
    private String title;
    private String content;
    private List<String> mediaUrls;  // Optional list of media URLs (images, videos, etc.)
    private int voteNumber;
    private VoteType userVotes;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isBlock;
    // private SimpleBadgeDTO badge;
}