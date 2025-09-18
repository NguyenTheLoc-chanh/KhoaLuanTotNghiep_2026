package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDto {
    private Long id;
    private Long userId;
    private Long roleId;
}
