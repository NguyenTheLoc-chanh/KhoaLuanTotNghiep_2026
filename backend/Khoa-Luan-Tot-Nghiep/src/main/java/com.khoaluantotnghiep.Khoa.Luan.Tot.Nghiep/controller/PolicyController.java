package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.PolicyDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
@Tag(name = "Policy", description = "API quản lý chính sách")
public class PolicyController {

    private final PolicyService policyService;

    @Operation(
            summary = "Tạo chính sách mới",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin chính sách",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"title\": \"Chính sách bảo mật\",\n" +
                                            "  \"description\": \"Nội dung chi tiết chính sách...\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tạo chính sách thành công"),
                    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> createPolicy(@Valid @RequestBody PolicyDto policyDto) {
        Response response = policyService.createPolicy(policyDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Lấy danh sách chính sách (có phân trang)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
                    @ApiResponse(responseCode = "404", description = "Không có chính sách nào")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> getAllPolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = policyService.getAllPolicies(page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Lấy chi tiết chính sách theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lấy chi tiết thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy chính sách")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> getPolicyById(@PathVariable Long id) {
        Response response = policyService.getPolicyById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Cập nhật chính sách theo ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"title\": \"Chính sách bảo mật (cập nhật)\",\n" +
                                            "  \"description\": \"Nội dung chính sách sau khi chỉnh sửa...\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy chính sách")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> updatePolicy(@PathVariable Long id, @Valid @RequestBody PolicyDto policyDto) {
        Response response = policyService.updatePolicy(id, policyDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Xóa chính sách theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Xóa thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy chính sách")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deletePolicy(@PathVariable Long id) {
        Response response = policyService.deletePolicy(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
