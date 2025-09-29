package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SampleCVRequest {
    private String title;
    private MultipartFile fCvFileFormat;
    private String description;
}
