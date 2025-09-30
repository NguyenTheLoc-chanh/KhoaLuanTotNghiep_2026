package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.FeedbackDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobCategoryDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Feedback;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobCategory;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class JobCategoryMapper {
    private final ModelMapper modelMapper;

    public JobCategoryMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public JobCategoryDto toDto(JobCategory jobCategory) {
        JobCategoryDto dto = modelMapper.map(jobCategory, JobCategoryDto.class);
        if (jobCategory.getUser() != null) {
            dto.setUserId(jobCategory.getUser().getUserId());
        }
        return dto;
    }

    public Feedback toEntity(FeedbackDto dto) {
        return modelMapper.map(dto, Feedback.class);
    }
}
