package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
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
            dto.setEmployeeId(jobPosting.getEmployee().getEmployeeId());
        }
        return dto;
    }

    public JobPosting toEntity(JobPostingDto dto) {
        return modelMapper.map(dto, JobPosting.class);
    }
}
