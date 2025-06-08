package com.mif.movieInsideForum.DTO;

import com.mif.movieInsideForum.Annotation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String displayName;
    private String email;
    @ValidPassword
    private String password;
    private String profilePictureUrl;
    private String bio;
}