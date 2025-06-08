package com.mif.movieInsideForum.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsPatchDTO {
    private String title;
    private String content;
    private String newsCategoryId;
    private List<String> mediaUrls;
    private List<String> tags;
}
