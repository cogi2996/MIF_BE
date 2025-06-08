package com.mif.movieInsideForum.Collection;

import lombok.Data;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "group_post_comments")
public class GroupPostComments {

    @Id
    private ObjectId id;

    private String postId;

    private String userId;

    private String comment;

    private Date createdAt;

    private Date updatedAt;
}
