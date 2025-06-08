package com.mif.movieInsideForum.Module.File;

import com.mif.movieInsideForum.DTO.ImgUploadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final S3Presigner presigner;
    @Value("${aws.s3.bucketName}")
    private String bucketName;
    private final S3Client s3Client;

    // Tạo presigned URL cho việc tải lên
    public ImgUploadResponseDTO generatePreSignedUrlForUpload(String originalFileName) {
        // Tạo tên tệp độc nhất
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
    
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .build();
    
        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(builder -> builder
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(10)) // Thời gian hết hạn của URL
        );
    
        return ImgUploadResponseDTO.builder()
                .presignedUrl(presignedRequest.url().toString())
                .fileName(uniqueFileName)
                .build();
    }

    public String getUploadedFileUrl(String uniqueFileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, "ap-southeast-1", uniqueFileName);
    }

    public void deleteFile(String uniqueFileName) {
        // Create a DeleteObjectRequest
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .build();

        // Delete the file from S3
        s3Client.deleteObject(deleteObjectRequest);
    }
}