package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller.publicapi;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.JobCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/job-categories")
@RequiredArgsConstructor
@Tag(name = "JobCategory", description = "API quản lý Danh mục ngành nghề Public")
public class JobCategoryPbController {
    private final JobCategoryService jobCategoryService;

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
    public ResponseEntity<Response> getAllJobCategories(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Response response = jobCategoryService.getAllJobCategories(page, size);
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
