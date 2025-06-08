package com.mif.movieInsideForum.Module.SentimentAnalysis;

import com.mif.movieInsideForum.DTO.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<?>> getPostStatistics() {
        // Implement the logic to get post statistics
        Object statistics = statisticsService.getPostStatistics();
        return ResponseEntity.ok(ResponseWrapper.builder()
                .status("success")
                .message("Post statistics")
                .data(statistics)
                .build());
    }


}