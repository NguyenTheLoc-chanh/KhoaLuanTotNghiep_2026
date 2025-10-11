package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller.publicapi;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "API quản lý nhà tuyển dụng (Employer) PUBLIC")
public class EmployeePbController {
    private final EmployeeService employeeService;

    @Operation(summary = "Lấy danh sách Employer (phân trang + search)", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<Response> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "employeeId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        Response response = employeeService.getAllEmployees(page, size, sortBy, sortDir, search);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
