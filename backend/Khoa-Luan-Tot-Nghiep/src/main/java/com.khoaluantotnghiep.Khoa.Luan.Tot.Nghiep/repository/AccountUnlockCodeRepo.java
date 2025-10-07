package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.AccountUnlockCode;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountUnlockCodeRepo extends JpaRepository<AccountUnlockCode, Long> {
    Optional<AccountUnlockCode> findByUser(User user);
    Optional<AccountUnlockCode> findByCode(String code);
    void deleteByUser(User user);
}
