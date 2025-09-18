package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.LoginRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.RegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;

public interface UserService {
    Response registerUser(RegisterRequest registrationRequest );
    Response loginUser(LoginRequest loginRequest );
    Response getAllUsers(int page, int size);
    User getLoginUser();
    Response logoutUser();
}
