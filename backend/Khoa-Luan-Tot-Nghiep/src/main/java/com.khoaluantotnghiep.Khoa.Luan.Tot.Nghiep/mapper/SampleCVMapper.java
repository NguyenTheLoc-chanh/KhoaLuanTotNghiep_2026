package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.SampleCVDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.SampleCV;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SampleCVMapper {
    private final ModelMapper modelMapper;

    public SampleCVMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SampleCVDto toDto(SampleCV sampleCV) {
        SampleCVDto dto = modelMapper.map(sampleCV, SampleCVDto.class);
        if (sampleCV.getUser() != null) {
            dto.setUserId(sampleCV.getUser().getUserId());
        }
        return dto;
    }

    public SampleCV toEntity(SampleCVDto dto) {
        return modelMapper.map(dto, SampleCV.class);
    }
}
