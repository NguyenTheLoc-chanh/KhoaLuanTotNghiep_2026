package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class UserLoginDto {
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private List<String> roles;
}
