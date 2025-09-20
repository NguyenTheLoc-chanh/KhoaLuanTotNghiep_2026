package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.GoogleLoginRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.GoogleAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/google")
@RequiredArgsConstructor
@Tag(name = "Google Authentication", description = "API đăng nhập bằng Google OAuth2")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    @Operation(
            summary = "Đăng nhập bằng Google OAuth2",
            description = "Sử dụng Google ID token để đăng nhập vào hệ thống. Hỗ trợ đăng nhập lại với tài khoản đã có.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Google ID token từ Google Sign-In",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"idToken\": \"eyJhbGciOiJSUzI1NiIsImtpZCI6IjE2NzI4...\",\n" +
                                            "  \"role\": \"CANDIDATE\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Đăng nhập Google thành công",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Đăng nhập Google thành công!\",\n" +
                                                    "  \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n" +
                                                    "  \"refreshToken\": \"abc123-refresh-token\",\n" +
                                                    "  \"roles\": [\"CANDIDATE\"],\n" +
                                                    "  \"userDto\": {\n" +
                                                    "    \"userId\": 1,\n" +
                                                    "    \"email\": \"user@gmail.com\",\n" +
                                                    "    \"fullName\": \"Nguyễn Văn A\",\n" +
                                                    "    \"phone\": null,\n" +
                                                    "    \"status\": \"ACTIVE\",\n" +
                                                    "    \"createdAt\": \"2025-09-20T11:20:42\",\n" +
                                                    "    \"updatedAt\": \"2025-09-20T11:21:42\",\n" +
                                                    "    \"roles\": [\"CANDIDATE\"]\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "ID token không hợp lệ hoặc đã hết hạn"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Xác thực Google thất bại"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<Response> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        Response response = googleAuthService.authenticateWithGoogle(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Lấy URL đăng nhập Google OAuth2",
            description = "Trả về URL để redirect user đến Google OAuth2 login",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "URL đăng nhập Google",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Google OAuth2 URL\",\n" +
                                                    "  \"googleAuthUrl\": \"https://accounts.google.com/o/oauth2/v2/auth?client_id=...&redirect_uri=...&response_type=code&scope=email%20profile&state=CANDIDATE\"\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/auth-url")
    public ResponseEntity<Response> getGoogleAuthUrl(@RequestParam(defaultValue = "CANDIDATE") String role) {
        Response response = googleAuthService.getGoogleAuthUrl(role);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Xử lý callback từ Google OAuth2",
            description = "Endpoint này được gọi sau khi user đăng nhập Google thành công. Hỗ trợ đăng nhập lại với tài khoản đã có.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Đăng nhập Google thành công",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Đăng nhập Google thành công!\",\n" +
                                                    "  \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n" +
                                                    "  \"refreshToken\": \"abc123-refresh-token\",\n" +
                                                    "  \"roles\": [\"CANDIDATE\"],\n" +
                                                    "  \"userDto\": {\n" +
                                                    "    \"userId\": 1,\n" +
                                                    "    \"email\": \"user@gmail.com\",\n" +
                                                    "    \"fullName\": \"Nguyễn Văn A\",\n" +
                                                    "    \"phone\": null,\n" +
                                                    "    \"status\": \"ACTIVE\",\n" +
                                                    "    \"createdAt\": \"2025-09-20T11:20:42\",\n" +
                                                    "    \"updatedAt\": \"2025-09-20T11:21:42\",\n" +
                                                    "    \"roles\": [\"CANDIDATE\"]\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/callback")
    public ResponseEntity<Response> handleGoogleCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error) {
        
        if (error != null) {
            Response errorResponse = Response.builder()
                    .status(400)
                    .message("Đăng nhập Google thất bại: " + error)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Response response = googleAuthService.handleGoogleCallback(code, state);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
