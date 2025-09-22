package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.JobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@RequestMapping("/api/job-postings")
@RequiredArgsConstructor
@Tag(name = "Job Postings", description = "API quản lý tin tuyển dụng")
public class JobPostingController {

    private final JobPostingService jobPostingService;

    // ===== CREATE =====
    @Operation(
            summary = "Tạo mới Job Posting",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = JobPostingDto.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"title\": \"Java Backend Developer\",\n" +
                                            "  \"description\": \"Phát triển dịch vụ RESTful API cho hệ thống HRM\",\n" +
                                            "  \"address\": \"Hà Nội\",\n" +
                                            "  \"jobField\": \"Software Engineering\",\n" +
                                            "  \"salary\": \"1200-1500 USD\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tạo thành công",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Job posting created successfully\",\n" +
                                                    "  \"jobPostingDto\": {\n" +
                                                    "    \"id\": 1,\n" +
                                                    "    \"title\": \"Java Backend Developer\",\n" +
                                                    "    \"address\": \"Hà Nội\",\n" +
                                                    "    \"jobField\": \"Software Engineering\",\n" +
                                                    "    \"salary\": \"1200-1500 USD\",\n" +
                                                    "    \"createdAt\": \"2025-09-22T08:00:00\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> createJobPosting(@Valid @RequestBody JobPostingDto dto) {
        Response response = jobPostingService.createJobPosting(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET ALL =====
    @Operation(summary = "Lấy tất cả tin tuyển dụng (phân trang)")
    @GetMapping
    public ResponseEntity<Response> getAllJobPostings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = jobPostingService.getAllJobPostings(page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET BY ID =====
    @Operation(summary = "Lấy tin tuyển dụng theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<Response> getJobPostingById(@PathVariable Long id) {
        Response response = jobPostingService.getJobPostingById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== UPDATE =====
    @Operation(
            summary = "Cập nhật Job Posting",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = JobPostingDto.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"title\": \"Java Senior Developer\",\n" +
                                            "  \"description\": \"Maintain and improve backend system\",\n" +
                                            "  \"address\": \"Hồ Chí Minh\",\n" +
                                            "  \"jobField\": \"IT\",\n" +
                                            "  \"salary\": \"1500-2000 USD\"\n" +
                                            "}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> updateJobPosting(
            @PathVariable Long id,
            @Valid @RequestBody JobPostingDto dto
    ) {
        Response response = jobPostingService.updateJobPosting(id, dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== DELETE =====
    @Operation(
            summary = "Xóa Job Posting",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Xóa thành công",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Job posting deleted successfully\"\n" +
                                                    "}"
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> deleteJobPosting(@PathVariable Long id) {
        Response response = jobPostingService.deleteJobPosting(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== SEARCH =====
    @Operation(
            summary = "Tìm kiếm Job Posting theo title keyword",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Danh sách job postings",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Search completed successfully\",\n" +
                                                    "  \"jobPostingDtoList\": [\n" +
                                                    "    {\n" +
                                                    "      \"id\": 1,\n" +
                                                    "      \"title\": \"Java Backend Developer\",\n" +
                                                    "      \"address\": \"Hà Nội\",\n" +
                                                    "      \"jobField\": \"Software Engineering\",\n" +
                                                    "      \"salary\": \"1200-1500 USD\"\n" +
                                                    "    }\n" +
                                                    "  ],\n" +
                                                    "  \"currentPage\": 0,\n" +
                                                    "  \"totalItems\": 1,\n" +
                                                    "  \"totalPages\": 1\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/search")
    public ResponseEntity<Response> searchJobPostings(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = jobPostingService.searchJobPostings(keyword, page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== FILTER =====
    @Operation(
            summary = "Lọc Job Posting theo địa chỉ và lĩnh vực",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Danh sách job postings",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Filter completed successfully\",\n" +
                                                    "  \"jobPostingDtoList\": [\n" +
                                                    "    {\n" +
                                                    "      \"id\": 2,\n" +
                                                    "      \"title\": \"Frontend Developer\",\n" +
                                                    "      \"address\": \"Đà Nẵng\",\n" +
                                                    "      \"jobField\": \"Web Development\",\n" +
                                                    "      \"salary\": \"800-1200 USD\"\n" +
                                                    "    }\n" +
                                                    "  ],\n" +
                                                    "  \"currentPage\": 0,\n" +
                                                    "  \"totalItems\": 1,\n" +
                                                    "  \"totalPages\": 1\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/filter")
    public ResponseEntity<Response> filterJobPostings(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = jobPostingService.filterJobPostings(location, jobType, page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== COMPANY POSTS =====
    @Operation(
            summary = "Lấy danh sách Job Posting theo Employee (công ty)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Danh sách job postings của công ty",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Company job postings retrieved successfully\",\n" +
                                                    "  \"jobPostingDtoList\": [\n" +
                                                    "    {\n" +
                                                    "      \"id\": 3,\n" +
                                                    "      \"title\": \"Project Manager\",\n" +
                                                    "      \"address\": \"Hồ Chí Minh\",\n" +
                                                    "      \"jobField\": \"Management\",\n" +
                                                    "      \"salary\": \"2000-2500 USD\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/company/{employeeId}")
    public ResponseEntity<Response> getJobPostingsByCompany(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = jobPostingService.getJobPostingsByCompany(employeeId, page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
