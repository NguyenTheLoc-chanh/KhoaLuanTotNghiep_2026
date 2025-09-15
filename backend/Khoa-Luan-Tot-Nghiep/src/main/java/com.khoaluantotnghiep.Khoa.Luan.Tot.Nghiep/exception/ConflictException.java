package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception;

//Khi có sự xung đột dữ liệu. - 409 Conflict
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
