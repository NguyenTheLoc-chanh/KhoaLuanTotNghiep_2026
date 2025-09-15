package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception;

//Khi client gửi dữ liệu không hợp lệ hoặc request không đúng format -400 Bad Request
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
