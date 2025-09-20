package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.RoleUser;

public interface UserService {
    User saveUserWithRole(RegisterRequest request, RoleUser roleEnum);
    Response registerCandidate(CandidateRegisterRequest registrationRequest );
    Response registerEmployer(EmployerRegisterRequest registrationRequest );
    Response loginUser(LoginRequest loginRequest );
    Response getAllUsers(int page, int size);
    User getLoginUser();
    Response logoutUser();
    Response changePassword(PasswordChangeRequest request);
    Response forgotPassword(String email);
    Response resetPassword(String token, String newPassword, String confirmPassword);
}
