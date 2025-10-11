package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.response.JobPostingCardDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobApplication;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class JobPostingMapper {
    private final ModelMapper modelMapper;

    public JobPostingMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public JobPostingDto toDto(JobPosting jobPosting) {
        JobPostingDto dto = modelMapper.map(jobPosting, JobPostingDto.class);
        if (jobPosting.getEmployee() != null) {
            dto.setEmployee(modelMapper.map(jobPosting.getEmployee(), EmployeeDto.class));
        }

        if (jobPosting.getJobCategory() != null) {
            dto.setJobCategory(modelMapper.map(jobPosting.getJobCategory(), JobCategoryDto.class));
        }
        return dto;
    }

    public JobPostingCardDto toJobPostingCardDto(JobPosting jobPosting) {
        return modelMapper.map(jobPosting, JobPostingCardDto.class);
    }

    public AppliedJobDto toAppliedJobDto(JobApplication jobApplication) {
        AppliedJobDto dto = modelMapper.map(jobApplication.getJobPosting(), AppliedJobDto.class);
        dto.setJobApplication(modelMapper.map(jobApplication, JobApplicationDto.class));
        return dto;
    }

    public JobPosting toEntity(JobPostingDto dto) {
        return modelMapper.map(dto, JobPosting.class);
    }
}
