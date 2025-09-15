package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception;

//Khi một resource (dữ liệu) không tồn tại trong hệ thống - 404 Not Found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
