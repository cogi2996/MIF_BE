// src/main/java/com/mif/movieInsideForum/DTO/CommentDTO.java
package com.mif.movieInsideForum.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Annotation.ObjectIdSetToStringSerializer;
import com.mif.movieInsideForum.Annotation.ObjectIdToStringSerializer;
import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.SimpleBadgeDTO;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Document(collection = "comments")
@AllArgsConstructor
@Getter
@Setter

public class Comment {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private   ObjectId id; // ID bình luận
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId postId; // ID bài viết
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId groupId; // ID nhóm
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId userId; // ID người dùng
    private String username; // Tên người dùng
    private String userAvatar = "https://mif-bucket-1.s3.ap-southeast-1.amazonaws.com/6163a3ff-370f-45cf-96cb-e2b6e037ea24_cho_ngao.jpg"; // Ảnh đại diện
    private String content;   // Nội dung bình luận
    @JsonSerialize(using = ObjectIdSetToStringSerializer.class)
    private Set<ObjectId> upvotes= new HashSet<>(); // Số lượng upvotes
    @JsonSerialize(using = ObjectIdSetToStringSerializer.class)
    private Set<ObjectId> downvotes =  new HashSet<>(); // Số lượng downvotes
    private Date createAt = new Date(); // Thời gian tạo bình luận
    
    // Thêm thông tin huy hiệu
    private SimpleBadgeDTO badge;
}