package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployerRegisterRequest  extends RegisterRequest{
    @Nullable
    private MultipartFile businessLicense;
}
