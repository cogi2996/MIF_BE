package com.mif.movieInsideForum.Collection;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {
    private ObjectId userId;  // Primary key
    @CreatedDate
    private Date joinedAt;
}