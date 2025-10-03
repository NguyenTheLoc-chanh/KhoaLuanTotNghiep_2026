package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.FeedbackDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Feedback;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {
    private final ModelMapper modelMapper;

    public FeedbackMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public FeedbackDto toDto(Feedback feedback) {
        FeedbackDto dto = modelMapper.map(feedback, FeedbackDto.class);
        if (feedback.getUser() != null) {
            dto.setUserId(feedback.getUser().getUserId());
            dto.setEmail(feedback.getUser().getEmail());
        }
        return dto;
    }

    public Feedback toEntity(FeedbackDto dto) {
        return modelMapper.map(dto, Feedback.class);
    }
}
