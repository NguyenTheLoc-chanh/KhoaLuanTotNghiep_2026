package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.JobCategoryRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.UserAuth;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.JobCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-categories")
@RequiredArgsConstructor
@Tag(name = "JobCategory", description = "API quản lý Danh mục ngành nghề")
public class JobCategoryController {

    private final JobCategoryService jobCategoryService;

    // ===== CREATE =====
    @Operation(
            summary = "Tạo mới danh mục ngành nghề",
            description = "Admin tạo danh mục ngành nghề",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JobCategoryRequest.class),
                            examples = @ExampleObject(
                                    name = "JobCategoryRequest",
                                    value = "{\n" +
                                            "  \"name\": \"Công nghệ thông tin\",\n" +
                                            "  \"description\": \"Lĩnh vực phần mềm, phần cứng, AI\"\n" +
                                            "}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> createJobCategory(
            @AuthenticationPrincipal UserAuth userAuth,
            @org.springframework.web.bind.annotation.RequestBody JobCategoryRequest request
    ) {
        Long userId = userAuth.getUserId();
        Response response = jobCategoryService.createJobCategory(userId, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET ALL =====
    @Operation(
            summary = "Lấy danh sách danh mục ngành nghề (có phân trang)",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Danh sách danh mục ngành nghề",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"Lấy danh sách danh mục công việc thành công\",\n" +
                                            "  \"jobCategoryDtoList\": [\n" +
                                            "    {\n" +
                                            "      \"jobCategoryId\": 1,\n" +
                                            "      \"name\": \"CNTT\",\n" +
                                            "      \"description\": \"Lĩnh vực công nghệ thông tin\"\n" +
                                            "    }\n" +
                                            "  ],\n" +
                                            "  \"currentPage\": 0,\n" +
                                            "  \"totalItems\": 1,\n" +
                                            "  \"totalPages\": 1\n" +
                                            "}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response> getAllJobCategories(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Response response = jobCategoryService.getAllJobCategories(page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET BY ID =====
    @Operation(
            summary = "Xem chi tiết danh mục ngành nghề theo ID",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Chi tiết danh mục",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"Lấy danh mục công việc thành công\",\n" +
                                            "  \"jobCategoryDto\": {\n" +
                                            "    \"jobCategoryId\": 1,\n" +
                                            "    \"name\": \"CNTT\",\n" +
                                            "    \"description\": \"Lĩnh vực công nghệ thông tin\"\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{jobCategoryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response> getJobCategoryById(@PathVariable Long jobCategoryId) {
        Response response = jobCategoryService.getJobCategoryById(jobCategoryId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== UPDATE =====
    @Operation(
            summary = "Cập nhật danh mục ngành nghề",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JobCategoryRequest.class),
                            examples = @ExampleObject(
                                    name = "JobCategoryRequest",
                                    value = "{\n" +
                                            "  \"name\": \"Công nghệ thông tin\",\n" +
                                            "  \"description\": \"Bao gồm AI, IoT, Cloud\"\n" +
                                            "}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{jobCategoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> updateJobCategory(
            @PathVariable Long jobCategoryId,
            @org.springframework.web.bind.annotation.RequestBody JobCategoryRequest request
    ) {
        Response response = jobCategoryService.updateJobCategory(jobCategoryId, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== DELETE =====
    @Operation(
            summary = "Xóa danh mục ngành nghề theo ID",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Xóa thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"Xóa danh mục công việc thành công\"\n" +
                                            "}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{jobCategoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteJobCategory(@PathVariable Long jobCategoryId) {
        Response response = jobCategoryService.deleteJobCategory(jobCategoryId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    // ===== STATS =====
    @Operation(
            summary = "Thống kê số lượng tin tuyển dụng theo danh mục ngành nghề",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Thống kê thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"Thống kê danh mục công việc thành công\",\n" +
                                            "  \"jobCategoryStats\": [\n" +
                                            "    {\n" +
                                            "      \"categoryName\": \"CNTT\",\n" +
                                            "      \"jobCount\": 15\n" +
                                            "    }\n" +
                                            "  ]\n" +"}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/stats")
    public ResponseEntity<Response> getJobCategoryStats() {
        Response response = jobCategoryService.getJobCategoryStats();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET JOBS BY CATEGORY =====
    @Operation(
            summary = "Lấy danh sách tin tuyển dụng theo danh mục (chỉ ACTIVE)",
            description = "Lấy tất cả các tin tuyển dụng có trạng thái ACTIVE trong danh mục theo ID, có phân trang.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Danh sách việc làm",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"Lấy danh sách việc làm theo danh mục thành công\",\n" +
                                            "  \"jobPostingDtoList\": [\n" +
                                            "    {\n" +
                                            "      \"jobId\": 1,\n" +
                                            "      \"title\": \"Java Developer\",\n" +
                                            "      \"salaryMin\": 1000,\n" +
                                            "      \"salaryMax\": 2000,\n" +
                                            "      \"status\": \"ACTIVE\"\n" +
                                            "    }\n" +
                                            "  ],\n" +
                                            "  \"currentPage\": 0,\n" +
                                            "  \"totalItems\": 1,\n" +
                                            "  \"totalPages\": 1\n" +
                                            "}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{jobCategoryId}/jobs")
    public ResponseEntity<Response> getJobPostingsByCategory(
            @PathVariable Long jobCategoryId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Response response = jobCategoryService.getAllJobPostingsByCategoryId(jobCategoryId, page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
