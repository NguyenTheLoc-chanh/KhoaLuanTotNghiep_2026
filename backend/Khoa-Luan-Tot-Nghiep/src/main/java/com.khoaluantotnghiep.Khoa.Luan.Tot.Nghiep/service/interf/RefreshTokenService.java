package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.RefreshToken;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    Optional<RefreshToken> verifyExpiration(RefreshToken token);
    void revokeToken(User user);
}
