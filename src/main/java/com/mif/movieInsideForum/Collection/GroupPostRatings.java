package com.mif.movieInsideForum.Collection;

import com.mif.movieInsideForum.Collection.Field.VoteType;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "group_post_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class GroupPostRatings {
    @Id
    private ObjectId id;
    private VoteType ratings;
    private ObjectId postId;
    private ObjectId groupId;
    private ObjectId userId;
    @CreatedDate
    private Date createdAt;
    
}
