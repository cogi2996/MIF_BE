package com.mif.movieInsideForum.DTO;

import com.mif.movieInsideForum.Collection.Award;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
@Builder
public class DirectorRequestDTO {
    private String name;
    private Date dateOfBirth;
    private String bio;
    private List<String> filmographyIds;
    private List<Award> awards;
    private String profilePictureUrl;

}
