package com.mif.movieInsideForum.Module.Post.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Field.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_posts")
public class ReportPost {
    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    private ObjectId id;
    
    private String postId;
    private String groupId;
    private String ownerId;
    private String ownerUsername;
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;
    
    @Builder.Default
    private int reportCount = 0;
    
    @Builder.Default
    private Date createdAt = new Date();
    
    @Builder.Default
    private Date updatedAt = new Date();
    @Builder.Default
    private List<GroupPostReport> groupReports = new ArrayList<>();
} 