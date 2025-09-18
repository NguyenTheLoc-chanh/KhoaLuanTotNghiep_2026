package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.RolePermissionDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.RolePermission;
import org.modelmapper.ModelMapper;

public class RolePermissionMapper {
    private final ModelMapper modelMapper;

    public RolePermissionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public RolePermissionDto toDto(RolePermission rolePermission) {
        RolePermissionDto dto = new RolePermissionDto();
        dto.setId(rolePermission.getId());
        dto.setRoleId(rolePermission.getRole().getRoleId());
        dto.setPermissionId(rolePermission.getPermission().getPermissionId());
        return dto;
    }

    public RolePermission toEntity(RolePermissionDto dto) {
        RolePermission rp = new RolePermission();
        rp.setId(dto.getId());
        // set Role, Permission ở service (cần load entity từ DB theo id)
        return rp;
    }
}
