package com.mif.movieInsideForum.DTO;

import com.mif.movieInsideForum.Collection.Field.Status;
import lombok.*;
import org.bson.types.ObjectId;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupStatus {
    private ObjectId groupId;
    private Status status;

}
