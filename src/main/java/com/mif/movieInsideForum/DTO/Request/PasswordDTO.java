package com.mif.movieInsideForum.DTO.Request;


import com.mif.movieInsideForum.Annotation.ValidPassword;
import lombok.Data;

@Data
public class PasswordDTO {
    @ValidPassword
    private String newPassword;
}