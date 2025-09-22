package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import lombok.Data;

@Data
public class LockUnlockRequest {
    private Long userId;
    private boolean lock; // true = khóa, false = mở khóa
}
