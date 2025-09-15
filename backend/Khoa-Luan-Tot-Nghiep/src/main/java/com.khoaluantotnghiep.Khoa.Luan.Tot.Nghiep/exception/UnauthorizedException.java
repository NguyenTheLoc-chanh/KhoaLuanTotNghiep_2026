package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception;

//Khi người dùng chưa đăng nhập hoặc không có token hợp lệ. - 401 Unauthorized
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
