package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.PolicyDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Policy;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PolicyMapper {
    private final ModelMapper modelMapper;

    public PolicyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PolicyDto toDto(Policy policy) {
        PolicyDto dto = modelMapper.map(policy, PolicyDto.class);
        if (policy.getUser() != null) {
            dto.setUserId(policy.getUser().getUserId());
        }
        return dto;
    }

    public Policy toEntity(PolicyDto dto) {
        return modelMapper.map(dto, Policy.class);
    }
}
