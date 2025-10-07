package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.LockUnlockRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.LoginRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.PasswordChangeRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.AccountUnlockCode;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.RefreshToken;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.UserStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ConflictException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.UserMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.JwtUtils;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.LoginAttemptConstants;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.EmailService;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final AccountUnlockCodeRepo accountUnlockCodeRepo;


    @Override
    public Response loginUser(LoginRequest loginRequest) {
        User user = userRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email đăng nhập không đúng"));
        if (user.getStatus() == UserStatus.LOCKED) {
            if (user.getLockUntil() != null) {
                LocalDateTime unlockTime = user.getLockUntil().plusMinutes(LoginAttemptConstants.LOCK_TIME_DURATION_MINUTES);
                if (LocalDateTime.now().isBefore(unlockTime)) {
                    long minutesLeft = java.time.Duration.between(LocalDateTime.now(), unlockTime).toMinutes();
                    throw new ConflictException("Tài khoản đang bị khóa. Vui lòng thử lại sau " + minutesLeft + " phút. Kiểm tra email để biết thêm chi tiết.");
                } else {
                    // hết thời gian lock -> mở khóa tự động
                    user.setStatus(UserStatus.ACTIVE);
                    user.setFailedLoginAttempts(0);
                    user.setLockUntil(null);
                    userRepo.save(user);
                }
            }
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            increaseFailedAttempts(user);
            throw new ResourceNotFoundException("Mật khẩu không chính xác");
        }
        resetFailedAttempts(user);

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
                .userLoginDto(userMapper.toLoginDto(user))
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

    @Override
    public Response sendUnlockAccountCode(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại trên hệ thống. Vui lòng kiểm tra lại."));
        if (user.getStatus() != UserStatus.LOCKED) {
            throw new ConflictException("Tài khoản chưa bị khóa. Vui lòng kiểm tra lại.");
        }
        accountUnlockCodeRepo.deleteByUser(user);
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        AccountUnlockCode unlockCode = AccountUnlockCode.builder()
                .code(code)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();
        accountUnlockCodeRepo.save(unlockCode);
        emailService.sendAccountUnlockCodeEmail(user.getEmail(), user.getFullName(), code);
        return Response.builder()
                .status(200)
                .message("Mã mở khóa đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư.")
                .build();
    }

    @Override
    public Response verifyUnlockAccountCode(String email, String code) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại trên hệ thống. Vui lòng kiểm tra lại."));
        if (user.getStatus() != UserStatus.LOCKED) {
            throw new ConflictException("Tài khoản chưa bị khóa. Vui lòng kiểm tra lại.");
        }
        AccountUnlockCode unlockCode = accountUnlockCodeRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Mã mở khóa không hợp lệ. Vui lòng kiểm tra lại."));
        if (unlockCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            accountUnlockCodeRepo.delete(unlockCode);
            throw new ConflictException("Mã mở khóa đã hết hạn. Vui lòng yêu cầu mã mới.");
        }
        if (!unlockCode.getCode().equals(code)) {
            throw new ConflictException("Mã mở khóa không hợp lệ. Vui lòng kiểm tra lại.");
        }
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
        userRepo.save(user);

        accountUnlockCodeRepo.delete(unlockCode);
        return Response.builder()
                .status(200)
                .message("Mở khóa tài khoản thành công! Vui lòng đăng nhập lại.")
                .build();
    }


    private void increaseFailedAttempts(User user) {
        int newFailCount = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newFailCount);

        if (newFailCount >= LoginAttemptConstants.MAX_FAILED_ATTEMPTS) {
            lockUser(user);
        }

        userRepo.save(user);
    }

    private void resetFailedAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
        userRepo.save(user);
    }

    private void lockUser(User user) {
        user.setStatus(UserStatus.LOCKED);
        user.setLockUntil(LocalDateTime.now());
        userRepo.save(user);

        // gửi email thông báo
        emailService.sendAccountLockEmail(user.getEmail());
    }

}
