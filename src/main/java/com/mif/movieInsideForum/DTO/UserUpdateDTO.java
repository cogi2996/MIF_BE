package com.mif.movieInsideForum.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mif.movieInsideForum.Collection.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateDTO {
    private String displayName;
    private String email;
    private String profilePictureUrl;
    private String bio;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date dob; // Change dob to Date

}