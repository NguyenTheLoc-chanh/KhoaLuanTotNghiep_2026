package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.RegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.UserDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Role;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.UserRole;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserDto toDto(User user) {
        UserDto dto = modelMapper.map(user, UserDto.class);
        if (user.getUserRoles() != null) {
            List<String> roles = user.getUserRoles().stream()
                    .map(UserRole::getRole)
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
            dto.setRoles(roles);
        }
        return dto;
    }

    public User toEntity(RegisterRequest registerRequest) {
        return modelMapper.map(registerRequest, User.class);
    }


    public User toEntity(UserDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
