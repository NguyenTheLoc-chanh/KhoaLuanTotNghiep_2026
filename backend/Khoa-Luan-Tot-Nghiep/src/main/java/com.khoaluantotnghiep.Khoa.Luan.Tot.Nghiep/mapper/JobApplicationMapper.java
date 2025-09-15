package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobApplicationDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobApplication;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class JobApplicationMapper {
    private final ModelMapper modelMapper;

    public JobApplicationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public JobApplicationDto toDto(JobApplication jobApplication) {
        JobApplicationDto dto = modelMapper.map(jobApplication, JobApplicationDto.class);
        if (jobApplication.getJobPosting() != null) {
            dto.setJobId(jobApplication.getJobPosting().getJobId());
        }
        if (jobApplication.getCandidate() != null) {
            dto.setCandidateId(jobApplication.getCandidate().getCandidateId());
        }
        return dto;
    }

    public JobApplication toEntity(JobApplicationDto dto) {
        return modelMapper.map(dto, JobApplication.class);
    }
}
