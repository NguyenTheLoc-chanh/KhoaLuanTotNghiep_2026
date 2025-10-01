package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.LockUnlockRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.PasswordChangeRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.RefreshToken;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.RefreshTokenRepository;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.JwtUtils;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl.RefreshTokenServiceImpl;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API quản lý đăng ký, đăng nhập, logout và refresh token")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepo;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final JwtUtils jwtUtils;

    // ===== LOGIN =====
    @Operation(
            summary = "Đăng nhập người dùng",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin login",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"email\": \"user@example.com\",\n" +
                                            "  \"password\": \"12345678\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Đăng nhập thành công",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Đăng nhập thành công!\",\n" +
                                                    "  \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n" +
                                                    "  \"refreshToken\": \"abc123-refresh-token\",\n" +
                                                    "  \"roles\": [\"CANDIDATE\"],\n" +
                                                    "  \"userDto\": {\n" +
                                                    "    \"id\": 1,\n" +
                                                    "    \"email\": \"user@example.com\",\n" +
                                                    "    \"role\": \"CANDIDATE\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "User không tồn tại hoặc mật khẩu sai")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        Response response = authService.loginUser(loginRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== LOGOUT =====
    @Operation(
            summary = "Logout người dùng hiện tại",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logout thành công",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Logout thành công!\"\n" +
                                                    "}"
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE','EMPLOYER')")
    public ResponseEntity<Response> logoutUser() {
        Response response = authService.logoutUser();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== REFRESH TOKEN =====
    @Operation(
            summary = "Refresh access token sử dụng refresh token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{ \"refreshToken\": \"abc123-refresh-token\" }"
                            ),
                            examples = @ExampleObject(
                                    name = "RefreshTokenRequest",
                                    value = "{ \"refreshToken\": \"abc123-refresh-token\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token refreshed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Token refreshed successfully\",\n" +
                                                    "  \"token\": \"new_access_token\",\n" +
                                                    "  \"refreshToken\": \"new_refresh_token\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Refresh token không tồn tại hoặc hết hạn"
                    )
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<Response> refreshToken(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");

        RefreshToken refreshToken = refreshTokenRepo.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại trong DB!"));

        // Verify token còn hạn
        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        // Tạo access token mới
        String newAccessToken = jwtUtils.generateToken(user);

        // Tạo refresh token mới
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        Response response = Response.builder()
                .status(200)
                .message("Token refreshed successfully")
                .token(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Đổi mật khẩu cho người dùng đang đăng nhập",
            description = "Người dùng cần cung cấp mật khẩu cũ và mật khẩu mới để cập nhật mật khẩu.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PasswordChangeRequest.class),
                            examples = @ExampleObject(
                                    name = "ChangePasswordRequest",
                                    value = "{\n" +
                                            "  \"oldPassword\": \"12345678\",\n" +
                                            "  \"newPassword\": \"123456789\",\n" +
                                            "  \"confirmNewPassword\": \"123456789\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Đổi mật khẩu thành công",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Đổi mật khẩu thành công! Vui lòng đăng nhập lại.\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dữ liệu không hợp lệ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 400,\n" +
                                                    "  \"message\": \"Mật khẩu mới và xác nhận mật khẩu không khớp.\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Lỗi xung đột dữ liệu (mật khẩu cũ sai)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 409,\n" +
                                                    "  \"message\": \"Mật khẩu cũ không chính xác.\"\n" +
                                                    "}"
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        Response response = authService.changePassword(request);
        return ResponseEntity.ok(response);
    }

    // ===== FORGOT PASSWORD =====
    @Operation(
            summary = "Yêu cầu đặt lại mật khẩu (Forgot password)",
            description = "Người dùng nhập email, hệ thống sẽ gửi link reset mật khẩu qua email.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{ \"email\": \"user@example.com\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email gửi thành công"),
                    @ApiResponse(responseCode = "404", description = "Email không tồn tại")
            }
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<Response> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Response response = authService.forgotPassword(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== RESET PASSWORD =====
    @Operation(
            summary = "Đặt lại mật khẩu (Reset password)",
            description = "Người dùng click link từ email (có token) để đặt mật khẩu mới.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"token\": \"jwt-reset-token\",\n" +
                                            "  \"newPassword\": \"12345678\",\n" +
                                            "  \"confirmPassword\": \"12345678\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Đặt lại mật khẩu thành công"),
                    @ApiResponse(responseCode = "400", description = "Mật khẩu không khớp"),
                    @ApiResponse(responseCode = "404", description = "Token hoặc email không hợp lệ")
            }
    )
    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");

        Response response = authService.resetPassword(token, newPassword, confirmPassword);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Khóa/Mở khóa user", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/lock-unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> lockUnlockUser(@RequestBody LockUnlockRequest request) {
        Response response = authService.lockUnlockUser(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
