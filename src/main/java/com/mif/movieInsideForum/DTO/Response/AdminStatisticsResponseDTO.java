package com.mif.movieInsideForum.DTO.Response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatisticsResponseDTO {
    private Long totalUser;
    private Long totalPost;
    private Long totalGroup;
    private Long totalMovie;
    private Long totalRatingMovie;
    private Long totalActor;

}
