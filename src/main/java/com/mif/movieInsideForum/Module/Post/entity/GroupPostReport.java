package com.mif.movieInsideForum.Module.Post.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPostReport {
    private String reporterId;
    private String reason;
    private Date reportedAt;

    public GroupPostReport(String reporterId, String reason) {
        this.reporterId = reporterId;
        this.reason = reason;
        this.reportedAt = new Date();
    }
} 