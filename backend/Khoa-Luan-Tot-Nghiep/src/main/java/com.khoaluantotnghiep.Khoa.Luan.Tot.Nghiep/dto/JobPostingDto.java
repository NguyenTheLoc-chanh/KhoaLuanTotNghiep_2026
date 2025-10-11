    package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import com.fasterxml.jackson.annotation.JsonInclude;
    import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.CandidateFavoriteJob;
    import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Employee;
    import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobApplication;
    import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobType;
    import jakarta.persistence.Column;
    import jakarta.persistence.EnumType;
    import jakarta.persistence.Enumerated;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.List;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public class JobPostingDto {
        private Long jobId;
        private String title;
        private String address;
        private LocalDate deadline;
        private Integer quantity;
        private String status;
        private LocalDateTime createdAt;
        private String jobBenefit;
        private String jobDescription;
        private String job_exp;
        private String jobRequirement;
        private String salary;
        private String type;
        private String workingTimes;

        private EmployeeDto employee;
        private JobCategoryDto jobCategory;
    }
