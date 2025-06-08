package com.mif.movieInsideForum.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Field.GroupType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponseDTO {
    @JsonSerialize(using = ToStringSerializer.class)

    private ObjectId id;
    private String groupName;
    private String description;
    private ObjectId ownerId;
    private String categoryId;
    private String avatarUrl;
    private Boolean isPublic; // Corrected field name
    private GroupType groupType;
    private Integer memberCount;
}