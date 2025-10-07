package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.EmployeeDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.response.EmployeeCardDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.response.EmployeeCardListDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Employee;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    private final ModelMapper modelMapper;

    public EmployeeMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public EmployeeDto toDto(Employee employee) {
        EmployeeDto dto = modelMapper.map(employee, EmployeeDto.class);
        if (employee.getUser() != null) {
            dto.setUserId(employee.getUser().getUserId());
            dto.setFullName(employee.getUser().getFullName());
            dto.setStatus(employee.getUser().getStatus().toString());
            dto.setCreatedAt(employee.getUser().getCreatedAt());
        }
        return dto;
    }

    public EmployeeCardDto toEmployeeCardDto(Employee employee) {
        return modelMapper.map(employee, EmployeeCardDto.class);
    }

    public EmployeeCardListDto toEmployeeCardListDto(Employee employee) {
        return modelMapper.map(employee, EmployeeCardListDto.class);
    }

    public Employee toEntity(EmployeeDto dto) {
        return modelMapper.map(dto, Employee.class);
    }
}
