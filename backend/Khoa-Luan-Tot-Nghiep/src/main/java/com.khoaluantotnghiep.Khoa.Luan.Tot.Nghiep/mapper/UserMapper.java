package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.CandidateRegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.RegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.UserDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.response.UserLoginDto;
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

    public UserLoginDto toLoginDto(User user) {
        if (user == null) return null;

        UserLoginDto dto = new UserLoginDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());

        // Chỉ map roles nếu userRoles không null
        if (user.getUserRoles() != null && !user.getUserRoles().isEmpty()) {
            List<String> roles = user.getUserRoles().stream()
                    .filter(ur -> ur.getRole() != null)
                    .map(ur -> ur.getRole().getRoleName())
                    .collect(Collectors.toList());
            dto.setRoles(roles);
        }

        return dto;
    }

    public User toEntity(RegisterRequest registerRequest) {
        return modelMapper.map(registerRequest, User.class);
    }

    public User toEntity(CandidateRegisterRequest registerCandidateRequest) {
        return modelMapper.map(registerCandidateRequest, User.class);
    }


    public User toEntity(UserDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
