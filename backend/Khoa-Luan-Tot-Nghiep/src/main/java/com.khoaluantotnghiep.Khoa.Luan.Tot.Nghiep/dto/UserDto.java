package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // (Jackson) → Khi convert sang JSON, bỏ qua các trường null, không trả về trong response.
@JsonIgnoreProperties(ignoreUnknown = true) // (Jackson) → Nếu JSON gửi lên có thêm các field lạ không khai báo trong class, sẽ bỏ qua, tránh lỗi parse.
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> roles;
}
