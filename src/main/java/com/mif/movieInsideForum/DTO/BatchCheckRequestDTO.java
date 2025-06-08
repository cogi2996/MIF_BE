package com.mif.movieInsideForum.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BatchCheckRequestDTO {
    private List<String> postIds;
}