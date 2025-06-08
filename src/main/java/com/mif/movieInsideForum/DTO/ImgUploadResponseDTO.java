package com.mif.movieInsideForum.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImgUploadResponseDTO {
    private String presignedUrl;
    private String fileName;

}
