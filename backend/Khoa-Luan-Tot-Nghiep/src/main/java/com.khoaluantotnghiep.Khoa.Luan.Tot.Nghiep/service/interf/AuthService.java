package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.LockUnlockRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.LoginRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.PasswordChangeRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;

public interface AuthService {
    Response loginUser(LoginRequest loginRequest );
    User getLoginUser();
    Response logoutUser();
    Response changePassword(PasswordChangeRequest request);
    Response forgotPassword(String email);
    Response resetPassword(String token, String newPassword, String confirmPassword);
    Response lockUnlockUser(LockUnlockRequest request);
}
