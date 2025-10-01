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
public class AppliedJobDto {
    private Long jobId;
    private String title;
    private String jobField;
    private String jobPosition;
    private String address;
    private Integer experienceYear;
    private Integer quantity;
    private Double salaryMin;
    private Double salaryMax;
    private Boolean negotiable;
    private LocalDateTime endDate;
    private String description;

    private EmployeeDto employee;
    private JobCategoryDto jobCategory;
    private JobApplicationDto jobApplication;
}
