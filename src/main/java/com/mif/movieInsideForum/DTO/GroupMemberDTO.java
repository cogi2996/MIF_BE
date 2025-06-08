// src/main/java/com/mif/movieInsideForum/DTO/GroupMemberDTO.java
package com.mif.movieInsideForum.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String displayName;
    private String avatar;
    private Date joinedAt;
}