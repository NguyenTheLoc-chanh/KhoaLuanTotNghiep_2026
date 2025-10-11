package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller.publicapi;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/policies")
@RequiredArgsConstructor
@Tag(name = "Policy", description = "API quản lý chính sách PUBLIC")
public class PolicyPbController {
    private final PolicyService policyService;

    @Operation(
            summary = "Lấy danh sách chính sách (có phân trang)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
                    @ApiResponse(responseCode = "404", description = "Không có chính sách nào")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<Response> getAllPolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = policyService.getAllPolicies(page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
