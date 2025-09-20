package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {
    @NotBlank(message = "ID token không được để trống")
    private String idToken;

    @NotBlank(message = "Role không được để trống")
    private String role;
}
