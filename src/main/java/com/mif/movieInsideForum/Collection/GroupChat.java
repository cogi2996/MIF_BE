package com.mif.movieInsideForum.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document(collection = "group_chats")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupChat {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId groupId; // = id group
    private String groupName;
    private String newestMessage;
    private Date updateTime;
    private String avatarUrl; // URL to the group's avatar image
}
