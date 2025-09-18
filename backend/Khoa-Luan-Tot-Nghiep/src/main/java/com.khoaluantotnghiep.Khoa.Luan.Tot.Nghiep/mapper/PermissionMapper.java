package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.PermissionDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Permission;
import org.modelmapper.ModelMapper;

public class PermissionMapper {
    private final ModelMapper modelMapper;

    public PermissionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PermissionDto toDto(Permission permission) {
        return modelMapper.map(permission, PermissionDto.class);
    }

    public Permission toEntity(PermissionDto dto) {
        return modelMapper.map(dto, Permission.class);
    }
}
