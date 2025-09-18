package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.RoleDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Role;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    private final ModelMapper modelMapper;

    public RoleMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public RoleDto toDto(Role role) {
        return modelMapper.map(role, RoleDto.class);
    }

    public Role toEntity(RoleDto dto) {
        return modelMapper.map(dto, Role.class);
    }
}
