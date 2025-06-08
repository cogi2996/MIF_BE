package com.mif.movieInsideForum.Collection.Notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class Notification {
    @Id
    @JsonProperty("notifyId")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId receiverId; // ID người nhận thông báo
    private String message; // Nội dung thông báo
    private NotificationType type; // Loại thông báo
    @Builder.Default
    private Date createdAt = new Date(); // Thời gian tạo thông báo
    @Builder.Default
    private Boolean isRead = false; // Trạng thái đã đọc hay chưa
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId groupId; // Đối tượng nhóm
    private String groupName; // Tên nhóm
    @Builder.Default
    private String groupAvatar ="https://mif-bucket-1.s3.ap-southeast-1.amazonaws.com/9eee504b-9d04-4fae-acec-32d81f520a53_defaul_background_group.png"; // Ảnh nhóm
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId groupPostId; // ID bài viết
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId senderId; // trường hợp vote, join
    @Builder.Default
    private Boolean isRemove = false; // Trạng thái đã xóa hay chưa
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId eventId; // ID sự kiện
    private String url = null; // Đường dẫn
    private String badgeLevel; // Add this field for badge level
    private String badgeName;  // Add this field for badge name

    // Getters and Setters
    //serialize voi ten la receiverId
    @JsonIgnore
    public String getReceiverIdAsString() {
        return receiverId != null ? receiverId.toHexString() : null;
    }
}