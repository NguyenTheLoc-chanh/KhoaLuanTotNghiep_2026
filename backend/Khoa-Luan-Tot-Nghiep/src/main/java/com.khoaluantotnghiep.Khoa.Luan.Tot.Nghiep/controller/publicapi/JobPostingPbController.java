package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller.publicapi;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.JobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/job-postings")
@RequiredArgsConstructor
@Tag(name = "Job Postings", description = "API quản lý tin tuyển dụng Public")
public class JobPostingPbController {
    private final JobPostingService jobPostingService;

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

    // ===== SEARCH =====
    @Operation(
            summary = "Tìm kiếm Job Posting theo keyword, location dành cho ADMIN",
            description = "Cho phép tìm kiếm tin tuyển dụng theo tiêu đề, tên công ty, địa điểm, có phân trang"
    )
    @GetMapping("/search")
    public ResponseEntity<Response> searchJobPostings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = jobPostingService.searchJobPostings(keyword, page, size, location);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== FILTER THE BETTER =====
    @Operation(
            summary = "Lọc Job Posting theo địa chỉ (the better)",
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
    @GetMapping("/filter-the-better")
    public ResponseEntity<Response> filterJobPostingsTheBetter(
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = jobPostingService.fileterJobPostingsTheBetter(location, page, size);
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

    // ===== SHARE JOB POSTING (EMPLOYER) =====
    @Operation(
            summary = "Tạo liên kết chia sẻ (share link) cho Job Posting",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tạo liên kết thành công",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Job posting share token generated successfully\",\n" +
                                                    "  \"shareLinkJob\": \"http://localhost:3000/public/job/eyJhbGciOiJIUzI1NiJ9...\"\n" +
                                                    "}"
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{jobId}/share")
    public ResponseEntity<Response> shareJobPosting(
            @PathVariable Long jobId
    ) {
        Response response = jobPostingService.shareJobPosting(jobId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // --- NEW METHOD ---
    // ===== GET JOB POSTING BY SHARE TOKEN (PUBLIC) =====
    @Operation(
            summary = "Lấy Job Posting bằng token chia sẻ (Public access)",
            description = "Sử dụng token được tạo từ /api/job-postings/{jobId}/share"

    )
    @GetMapping("/public/share-token")
    public ResponseEntity<Response> getJobPostingByShareToken(@RequestParam String token) {
        Response response = jobPostingService.getJobPostingByShareToken(token);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET BY ADDRESS =====
    @Operation(
            summary = "Lấy danh sách tin tuyển dụng theo địa chỉ",
            description = "Lấy danh sách job postings theo địa chỉ (phân trang, mặc định sort mới nhất)"
    )
    @GetMapping("/by-address")
    public ResponseEntity<Response> getJobPostingsByAddress(
            @RequestParam(required = false) String address,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = jobPostingService.getJobPostingsByAddress(address, page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Lọc tin tuyển dụng theo nhiều tiêu chí: tiêu đề, tên công ty, địa chỉ, mức lương dành cho trang chủ")
    @GetMapping("/list-filter")
    public ResponseEntity<Response> filterJobPostings(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String salaryRange,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = jobPostingService.filterJobPostings(title, companyName, address, salaryRange, page, size);
        return ResponseEntity.ok(response);
    }
}
