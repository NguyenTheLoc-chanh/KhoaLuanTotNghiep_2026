package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service;

import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.cloudinary.Cloudinary;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
    // Upload ảnh
    public String uploadImage(MultipartFile file) {
        return upload(file, "image");
    }

    // Upload file PDF (raw file)
    public String uploadPdf(MultipartFile file) {
        return upload(file, "raw");
    }

    // Upload file lớn sử dụng chunking
    public String uploadLargePdf(MultipartFile file) {
        try {
            // Sử dụng uploadLarge thay vì upload
            Map uploadResult = cloudinary.uploader().uploadLarge(file.getInputStream(),
                    ObjectUtils.asMap("resource_type", "raw"));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Upload large PDF error: {}", e.getMessage());
            throw new RuntimeException("Unable to upload large PDF: " + e.getMessage());
        } catch (Exception e) {
            // uploadLarge có thể ném ra Exception chung
            log.error("Upload large PDF failed unexpectedly: {}", e.getMessage());
            throw new RuntimeException("Upload large PDF failed: " + e.getMessage());
        }
    }

    private String upload(MultipartFile file, String resourceType) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", resourceType));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Upload {} error: {}", resourceType, e.getMessage());
            throw new RuntimeException("Unable to upload " + resourceType + ": " + e.getMessage());
        }
    }

    // Upload nhiều ảnh
    public List<String> uploadImages(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(uploadImage(file));
        }
        return urls;
    }
}
