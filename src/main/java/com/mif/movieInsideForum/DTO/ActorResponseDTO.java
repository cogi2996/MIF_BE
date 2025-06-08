package com.mif.movieInsideForum.DTO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Award;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ActorResponseDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String name;
    private Date dateOfBirth;
    private String bio;
    private List<Award> awards; // list of awards
    private String profilePictureUrl;
    private Date createdAt;
    private Date updatedAt;
    private Double scoreRank;
    private Double previousScoreRank;
    private Integer favoriteCount = 0;
}
