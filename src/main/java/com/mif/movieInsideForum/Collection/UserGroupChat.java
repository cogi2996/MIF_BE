package com.mif.movieInsideForum.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "user_group_chats")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGroupChat {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId userId;
    @DocumentReference(collection = "group_chats")
    private Map<ObjectId, GroupChat> groupChats = new HashMap<>();
    // Hàm trả danh sách groupChats được sắp xếp theo updateTime
    @JsonProperty("groupChats")
    public List<GroupChat> getSortedGroupChats() {
        return groupChats.values()
                .stream()
                .sorted(Comparator.comparing(GroupChat::getUpdateTime).reversed())
                .toList();
    }

}
