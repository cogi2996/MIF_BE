package com.mif.movieInsideForum.Module.ActivityAnalytics.controller;

import com.mif.movieInsideForum.DTO.ResponseWrapper;
import com.mif.movieInsideForum.Module.ActivityAnalytics.service.ActivityScoreService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activity-analytics")
@RequiredArgsConstructor
public class BadgeInfoController {
    private final ActivityScoreService activityScoreService;

    @GetMapping("/top-active-users")
    public ResponseEntity<ResponseWrapper<List<Map<String, Object>>>> getTopActiveUsers(@RequestParam String groupId) {
        List<Map<String, Object>> topUsers = activityScoreService.getTopActiveUsersInGroup(groupId, 10);
        return ResponseEntity.ok(ResponseWrapper.<List<Map<String, Object>>>builder()
                .status("success")
                .message("Top 10 người dùng hoạt động nhiều nhất")
                .data(topUsers)
                .build());
    }
} 