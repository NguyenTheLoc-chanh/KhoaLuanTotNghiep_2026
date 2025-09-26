package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.AdminRegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.CandidateRegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.EmployerRegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "API quản lý người dùng (đăng ký)")
public class UserController {

    private final UserService userService;
    // ===== REGISTER =====
    @Operation(
            summary = "Đăng ký ứng viên (Candidate)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin user cần đăng ký",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"fullName\": \"Nguyễn Thế Lộc\",\n" +
                                            "  \"email\": \"user@example.com\",\n" +
                                            "  \"password\": \"12345678\",\n" +
                                            "  \"confirmPassword\": \"12345678\",\n" +
                                            "  \"role\": \"CANDIDATE\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Đăng ký thành công",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 201,\n" +
                                                    "  \"message\": \"Đăng ký thành công\",\n" +
                                                    "  \"userDto\": {\n" +
                                                    "    \"id\": 1,\n" +
                                                    "    \"email\": \"nguyentheloc@gmail.com\",\n" +
                                                    "    \"role\": \"CANDIDATE\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc mật khẩu không khớp"),
                    @ApiResponse(responseCode = "409", description = "Email đã tồn tại")
            }
    )
    @PostMapping("/register/candidate")
    public ResponseEntity<Response> registerCandidate(@Valid @RequestBody CandidateRegisterRequest registerRequest) {
        Response response = userService.registerCandidate(registerRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @Operation(
            summary = "Đăng ký nhà tuyển dụng (Employer) - multipart/form-data",
            description = "Đăng ký user role EMPLOYER. Gửi form-data, field businessLicense là file PDF (nếu có).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(
                                    // Hiển thị ví dụ form-data (Swagger UI sẽ hiện mẫu này trong phần requestBody)
                                    example = "fullName=Nguyễn Thế Lộc\n" +
                                            "email=employer@example.com\n" +
                                            "password=password123\n" +
                                            "confirmPassword=password123\n" +
                                            "role=EMPLOYER\n" +
                                            "businessLicense=(file - PDF)"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Đăng ký thành công (nhà tuyển dụng)",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 201,\n" +
                                                    "  \"message\": \"Đăng ký thành công nhà tuyển dụng!\",\n" +
                                                    "  \"userDto\": {\n" +
                                                    "    \"userId\": 124,\n" +
                                                    "    \"email\": \"employer@example.com\",\n" +
                                                    "    \"fullName\": \"Nguyễn Thế Lộc\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    ))),
                    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
                    @ApiResponse(responseCode = "409", description = "Email đã tồn tại")
            }
    )
    @PostMapping(value = "/register/employer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> registerEmployer(@Valid @ModelAttribute EmployerRegisterRequest registerRequest) {
        Response response = userService.registerEmployer(registerRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @Operation(
            summary = "Đăng ký admin (Admin)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin user cần đăng ký",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"fullName\": \"Admin\",\n" +
                                            "  \"email\": \"admin@gmail.com\",\n" +
                                            "  \"password\": \"12345678\",\n" +
                                            "  \"confirmPassword\": \"12345678\",\n" +
                                            "  \"role\": \"ADMIN\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Đăng ký thành công",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 201,\n" +
                                                    "  \"message\": \"Đăng ký thành công\",\n" +
                                                    "  \"userDto\": {\n" +
                                                    "    \"id\": 1,\n" +
                                                    "    \"email\": \"nguyentheloc@gmail.com\",\n" +
                                                    "    \"role\": \"CANDIDATE\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc mật khẩu không khớp"),
                    @ApiResponse(responseCode = "409", description = "Email đã tồn tại")
            }
    )
    @PostMapping("/register/admin")
    public ResponseEntity<Response> registerAdmin(@Valid @RequestBody AdminRegisterRequest registerRequest) {
        Response response = userService.registerAdmin(registerRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
