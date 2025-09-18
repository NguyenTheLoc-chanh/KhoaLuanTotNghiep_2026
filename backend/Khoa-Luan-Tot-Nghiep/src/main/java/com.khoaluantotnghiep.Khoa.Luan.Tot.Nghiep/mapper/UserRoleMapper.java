package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.UserRoleDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.UserRole;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserRoleMapper {
    private final ModelMapper modelMapper;

    public UserRoleMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    public UserRoleDto toDto(UserRole userRole) {
        UserRoleDto dto = new UserRoleDto();
        dto.setId(userRole.getId());
        dto.setUserId(userRole.getUser().getUserId());
        dto.setRoleId(userRole.getRole().getRoleId());
        return dto;
    }
    public UserRole toEntity(UserRoleDto dto) {
        UserRole ur = new UserRole();
        ur.setId(dto.getId());
        // set User, Role ở service (cần load entity từ DB theo id)
        return ur;
    }
}
