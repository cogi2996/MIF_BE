package com.mif.movieInsideForum.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Field.GroupType;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.sql.Array;
import java.util.*;

@Document(collection = "groups")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Group implements Serializable {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;  // Group ID
    private String groupName;
    private String description;
    @Builder.Default
    private String avatarUrl = "https://mif-bucket-1.s3.ap-southeast-1.amazonaws.com/9eee504b-9d04-4fae-acec-32d81f520a53_defaul_background_group.png";
    @DBRef(lazy = false)
    private User owner;  // user_id of the group owner
    @DBRef(lazy = true)
    private MovieCategory category;  // Reference to the GroupCategory collection
    private List<GroupMember> members = new ArrayList<>();  // Initialize to an empty list
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
    private Boolean isPublic;
    private GroupType groupType = GroupType.SMALL;
    @DBRef(lazy = true)
    private List<User> pendingInvitations = new ArrayList<>();  // Array of user_ids
    private List<GroupRule> ruleList = new ArrayList<>();
}