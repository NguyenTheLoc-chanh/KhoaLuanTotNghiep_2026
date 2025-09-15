package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationDto {
    private Long jobApplicationId;
    private String fullName;
    private String email;
    private String phone;
    private String fCv;
    private LocalDateTime appliedAt;
    private String status;

    private Long jobId;
    private Long candidateId;
}
