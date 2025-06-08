package com.mif.movieInsideForum.Module.Post.DTO;

import com.mif.movieInsideForum.DTO.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;


import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPostRequestDTO {
    private ObjectId groupId;
    private UserDTO owner;
    private String title;
    private String content;
    private List<String> mediaUrls;  // Optional list of media URLs (images, videos, etc.)
    private int userVotes;
}
