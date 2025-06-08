package com.mif.movieInsideForum.DTO;

import com.mif.movieInsideForum.Collection.Award;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ActorRequestDTO {
    private String name;
    private Date dateOfBirth;
    private String bio;
    private List<String> filmographyIds; // list of movies
    private List<Award> awards; // list of awards
    private String profilePictureUrl;
}