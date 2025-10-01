package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.GoogleLoginRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;

public interface GoogleAuthService {
    Response authenticateWithGoogle(GoogleLoginRequest request);
    Response getGoogleAuthUrl(String role);
    Response handleGoogleCallback(String code, String state);
    String exchangeCodeForAccessToken(String code);
    void handleReLogin(User user);
    void handleRoleChangeIfNeeded(User user, String requestedRole);
}
