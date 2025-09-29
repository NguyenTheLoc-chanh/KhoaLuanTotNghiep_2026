package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.SampleCVRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.UserAuth;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.SampleCVService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/sample-cvs")
@RequiredArgsConstructor
@Tag(name = "SampleCV", description = "API quản lý mẫu CV")
public class SampleCVController {

    private final SampleCVService sampleCVService;

    // ===== CREATE =====
    @Operation(
            summary = "Tạo mẫu CV",
            description = "Ứng viên có thể tạo Sample CV (title, description, upload file PDF)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tạo thành công",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(example = "{\n" +
                                            "  \"status\": 201,\n" +
                                            "  \"message\": \"Tạo mẫu CV thành công!\",\n" +
                                            "  \"sampleCVDto\": {\n" +
                                            "    \"id\": 1,\n" +
                                            "    \"title\": \"CV IT Fresher\",\n" +
                                            "    \"description\": \"Mẫu CV chuẩn ngành IT\",\n" +
                                            "    \"fCvFileFormat\": \"https://res.cloudinary.com/.../cv.pdf\"\n" +
                                            "  }\n" +
                                            "}"))),
                    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> createSampleCV(
            @AuthenticationPrincipal UserAuth userAuth,
            @RequestPart("sampleCVRequest") String requestJson,
            @RequestPart(value = "fCvFile", required = false) MultipartFile fCvFile
    ) throws JsonProcessingException {
        // Convert JSON string -> SampleCVRequest
        ObjectMapper objectMapper = new ObjectMapper();
        SampleCVRequest request = objectMapper.readValue(requestJson, SampleCVRequest.class);

        if (fCvFile != null && !fCvFile.isEmpty()) {
            request.setFCvFileFormat(fCvFile);
        }

        Response response = sampleCVService.createSampleCV(userAuth.getUserId(), request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


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
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER','CANDIDATE')")
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
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER','CANDIDATE')")
    public ResponseEntity<Response> getSampleCVById(@PathVariable Long sampleCVId) {
        Response response = sampleCVService.getSampleCVById(sampleCVId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== UPDATE =====
    @Operation(summary = "Cập nhật SampleCV")
    @PutMapping(value = "/{sampleCVId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Response> updateSampleCV(
            @PathVariable Long sampleCVId,
            @RequestPart("sampleCVRequest") @Valid SampleCVRequest request,
            @RequestPart(value = "fCvFile", required = false) MultipartFile fCvFile
    ) {
        if (fCvFile != null && !fCvFile.isEmpty()) {
            request.setFCvFileFormat(fCvFile);
        }
        Response response = sampleCVService.updateSampleCV(sampleCVId, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== DELETE =====
    @Operation(summary = "Xóa SampleCV theo ID")
    @DeleteMapping("/{sampleCVId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> deleteSampleCV(@PathVariable Long sampleCVId) {
        Response response = sampleCVService.deleteSampleCV(sampleCVId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
