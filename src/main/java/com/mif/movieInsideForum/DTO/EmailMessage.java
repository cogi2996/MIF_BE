package com.mif.movieInsideForum.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage implements Serializable {
    private String email;   // Địa chỉ email người nhận
    private String subject; // Tiêu đề của email
    private String body;    // Nội dung của email
}