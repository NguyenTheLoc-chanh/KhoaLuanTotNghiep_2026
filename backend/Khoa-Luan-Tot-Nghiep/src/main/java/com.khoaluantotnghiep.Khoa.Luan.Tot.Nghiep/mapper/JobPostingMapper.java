package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.AppliedJobDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobApplicationDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
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
        return modelMapper.map(jobPosting, JobPostingDto.class);
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
