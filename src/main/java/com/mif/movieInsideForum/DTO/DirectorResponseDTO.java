package com.mif.movieInsideForum.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Award;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectorResponseDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String name;
    private Date dateOfBirth;
    private String bio;
    private List<Award> awards;
    private String profilePictureUrl;
    private Date createdAt;
    private Date updatedAt;

}
