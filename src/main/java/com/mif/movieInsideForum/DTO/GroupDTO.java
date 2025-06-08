package com.mif.movieInsideForum.DTO;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Field.GroupType;
import lombok.*;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String groupName;
    private String description;
    private UserDTO owner;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId categoryId;
    private String avatarUrl;
    private Boolean isPublic; // Corrected field name
    private GroupType groupType;
    private Integer memberCount;
    private Long weeklyPostCount; // New field for weekly post count
    private Date createdAt;
    // nếu null thì khong hiển thị
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date updatedAt;

}