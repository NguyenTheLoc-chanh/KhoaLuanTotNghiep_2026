package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller.publicapi;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.SampleCVService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/sample-cvs")
@RequiredArgsConstructor
@Tag(name = "SampleCV", description = "API quản lý mẫu CV Public")
public class SampleCVPbController {
    private final SampleCVService sampleCVService;

    // ===== GET ALL (PAGINATION) =====
    @Operation(
            summary = "Lấy danh sách SampleCV (có phân trang, tìm kiếm, sắp xếp)",
            description = "Query params: page, size, sortBy, sortDir, search",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Thành công")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<Response> getAllSampleCVs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sampleCVId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search
    ) {
        Response response = sampleCVService.getAllSampleCVs(page, size, sortBy, sortDir, search);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET BY ID =====
    @Operation(summary = "Lấy thông tin SampleCV theo ID")
    @GetMapping("/{sampleCVId}")
    public ResponseEntity<Response> getSampleCVById(@PathVariable Long sampleCVId) {
        Response response = sampleCVService.getSampleCVById(sampleCVId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    // ===== DOWNLOAD FILE =====
    @Operation(summary = "Tải trực tiếp file mẫu CV theo ID")
    @GetMapping("/{sampleCVId}/download")
    public ResponseEntity<Resource> downloadSampleCVFile(@PathVariable Long sampleCVId) {
        return sampleCVService.downloadSampleCVFile(sampleCVId);
    }
}
