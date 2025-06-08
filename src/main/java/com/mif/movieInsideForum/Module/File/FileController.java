package com.mif.movieInsideForum.Module.File;

import com.mif.movieInsideForum.DTO.ImgUploadResponseDTO;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // Endpoint để tạo presigned URL cho việc tải lên
    @GetMapping("/generate-presigned-url/upload")
    public ResponseEntity<ResponseWrapper<ImgUploadResponseDTO>> generatePresignedUrlForUpload(@RequestParam("fileName") String fileName) {
        ImgUploadResponseDTO imgUploadResponseDTO = fileService.generatePreSignedUrlForUpload(fileName);
        ResponseWrapper<ImgUploadResponseDTO> responseWrapper = ResponseWrapper.<ImgUploadResponseDTO>builder()
                .status("success")
                .message("Presigned URL generated successfully")
                .data(imgUploadResponseDTO)
                .build();
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

    // Endpoint để lấy URL của ảnh đã tải lên
    @GetMapping("/uploaded-file-url")
    public ResponseEntity<ResponseWrapper<String>> getUploadedFileUrl(@RequestParam("fileName") String fileName) {
        String fileUrl = fileService.getUploadedFileUrl(fileName);
        ResponseWrapper<String> responseWrapper = ResponseWrapper.<String>builder()
                .status("success")
                .message("File URL retrieved successfully")
                .data(fileUrl)
                .build();
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }
}