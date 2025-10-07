package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class JobPostingCardDto {
    private Long jobId;
    private String title;
    private String address;
    private Double salaryMin;
    private Double salaryMax;
    private Boolean negotiable;
    private String status;

    private EmployeeCardDto employee;
}
