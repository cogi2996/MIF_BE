package com.mif.movieInsideForum.Collection;

import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "saved_posts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SavedPost {
    @Id
    private ObjectId id;
    @DBRef(lazy = true)
    private User user;
    @DBRef(lazy = false)
    private GroupPost post;
    private ObjectId groupId; // Reference to the Group entity
    @CreatedDate
    private Date savedAt;
}