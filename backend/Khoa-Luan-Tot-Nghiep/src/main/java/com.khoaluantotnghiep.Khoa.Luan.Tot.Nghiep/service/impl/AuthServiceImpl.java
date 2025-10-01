package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.LockUnlockRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.LoginRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.PasswordChangeRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.RefreshToken;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.UserStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ConflictException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.UserMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.JwtUtils;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.EmailService;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;


    @Override
    public Response loginUser(LoginRequest loginRequest) {
        User user = userRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với email: " + loginRequest.getEmail()));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Mật khẩu không chính xác");
        }
        String token = jwtUtils.generateToken(user);
        // Lấy thời gian hết hạn token -> ISO 8601
        String expirationStr = jwtUtils.getExpirationFromToken(token)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        // Lấy roles từ user
        List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleName())
                .toList();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return Response.builder()
                .status(200)
                .message("Đăng nhập thành công!")
                .token(token)
                .refreshToken(refreshToken.getToken())
                .expirationTime(expirationStr)
                .roles(roles)
                .userDto(userMapper.toDto(user))
                .build();
    }

    @Override
    public User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Kiểm tra xem user đã đăng nhập hợp lệ chưa
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Bạn chưa đăng nhập hoặc phiên đã hết hạn");
        }
        String email = authentication.getName();

        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));
    }

    @Override
    public Response logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Bạn chưa đăng nhập hoặc phiên đã hết hạn");
        }

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Email: " + email));

        refreshTokenService.revokeToken(user);

        return Response.builder()
                .status(200)
                .message("Logout thành công!")
                .build();
    }

    @Override
    public Response changePassword(PasswordChangeRequest request) {
        User currentUser = getLoginUser();
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new ConflictException("Mật khẩu cũ không chính xác.");
        }
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new ConflictException("Mật khẩu mới và xác nhận mật khẩu không khớp.");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(currentUser);
        refreshTokenService.revokeToken(currentUser);
        return Response.builder()
                .status(200)
                .message("Đổi mật khẩu thành công! Vui lòng đăng nhập lại.")
                .build();
    }

    @Override
    public Response forgotPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại trên hệ thống. Vui lòng kiểm tra lại."));

        // Tạo JWT reset token
        String resetToken = jwtUtils.generateResetPasswordToken(user.getEmail());

        // Link reset
        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;

        // Gửi email
        emailService.sendResetPasswordEmail(user.getEmail(), resetLink, 5);

        return Response.builder()
                .status(200)
                .message("Hãy kiểm tra email của bạn. Sau đó nhấn vào link trong hộp thư để đổi lại mật khẩu.")
                .build();
    }

    @Override
    public Response resetPassword(String token, String newPassword, String confirmPassword) {
        String email = jwtUtils.validateResetPasswordToken(token);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại trên hệ thống. Vui lòng kiểm tra lại."));

        if (!newPassword.equals(confirmPassword)) {
            throw new ConflictException("Mật khẩu mới và xác nhận mật khẩu không khớp.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // Hủy refresh token cũ để bắt buộc login lại
        refreshTokenService.revokeToken(user);

        return Response.builder()
                .status(200)
                .message("Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.")
                .build();
    }

    @Override
    public Response lockUnlockUser(LockUnlockRequest request) {
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy user với id: " + request.getUserId()));

        if (request.isLock()) {
            user.setStatus(UserStatus.LOCKED);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }

        userRepo.save(user);

        return Response.builder()
                .status(200)
                .message(request.isLock()
                        ? "Khóa tài khoản thành công"
                        : "Mở khóa tài khoản thành công")
                .userDto(userMapper.toDto(user))
                .build();
    }
}
