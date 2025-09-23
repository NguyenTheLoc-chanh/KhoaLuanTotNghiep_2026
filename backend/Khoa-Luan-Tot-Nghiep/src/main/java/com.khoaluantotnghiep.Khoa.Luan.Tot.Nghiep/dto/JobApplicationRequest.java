package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequest {
    private Long candidateId;
    private Long jobId;
    private String fullName;
    private String email;
    private String phone;
}
