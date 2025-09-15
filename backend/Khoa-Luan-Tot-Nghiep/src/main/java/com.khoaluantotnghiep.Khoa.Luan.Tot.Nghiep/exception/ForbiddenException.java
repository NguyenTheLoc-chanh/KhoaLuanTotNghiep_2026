package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception;

//Người dùng đã login nhưng không có quyền truy cập. - 403 Forbidden
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
