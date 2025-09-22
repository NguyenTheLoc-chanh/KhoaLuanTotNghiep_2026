package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private Long employeeId;
    private String avatar;
    private String address;
    private String scale;
    private String description;
    private String companyName;
    private String companyLogo;
    private String website;
    private String businessLicense;

    private Long userId;
    private String fullName;
    private String status;
}
