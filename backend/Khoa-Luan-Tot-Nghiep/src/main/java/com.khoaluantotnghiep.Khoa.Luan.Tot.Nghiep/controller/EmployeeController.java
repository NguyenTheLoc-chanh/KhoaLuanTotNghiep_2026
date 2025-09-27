package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.EmployeeDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.EmployeeInfoCompanyRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.UserAuth;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "API quản lý nhà tuyển dụng (Employer)")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Tạo mới Employer", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        Response response = employeeService.createEmployee(employeeDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Cập nhật thông tin công ty của Employer", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/company-info")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> updateEmployeeInfoCompany(
            @AuthenticationPrincipal UserAuth userAuth,
            @Valid @RequestBody EmployeeInfoCompanyRequest request) {
        Long userId = userAuth.getUserId();
        Response response = employeeService.updateEmployeeInfoCompany(userId, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Upload Avatar Employer", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> uploadAvatar(
            @AuthenticationPrincipal UserAuth userAuth,
            @RequestParam("avatar") MultipartFile avatarFile) {
        Long userId = userAuth.getUserId();
        Response response = employeeService.uploadAvatar(userId, avatarFile);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Upload Logo Công ty", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> uploadCompanyLogo(
            @AuthenticationPrincipal UserAuth userAuth,
            @RequestParam("companyLogo") MultipartFile companyLogoFile) {
        Long userId = userAuth.getUserId();
        Response response = employeeService.uploadCompanyLogo(userId, companyLogoFile);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Upload Giấy phép kinh doanh (PDF)", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/business-license", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> uploadBusinessLicense(
            @AuthenticationPrincipal UserAuth userAuth,
            @RequestParam("businessLicense") MultipartFile licenseFile) {
        Long userId = userAuth.getUserId();
        Response response = employeeService.uploadBusinessLicense(userId, licenseFile);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Lấy thông tin Employer theo userId", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER')")
    public ResponseEntity<Response> getEmployeeByUserId(@PathVariable Long userId) {
        Response response = employeeService.getEmployeeByUserId(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Lấy danh sách Employer (phân trang + search)", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "employeeId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        Response response = employeeService.getAllEmployees(page, size, sortBy, sortDir, search);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Xóa Employer theo userId", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteEmployee(@PathVariable Long userId) {
        Response response = employeeService.deleteEmployee(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
