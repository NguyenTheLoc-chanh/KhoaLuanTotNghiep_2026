package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.UserAuth;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Response> createJobPosting(
            @AuthenticationPrincipal UserAuth userAuth,
            @Valid @RequestBody JobPostingDto dto
    ) {
        Response response = jobPostingService.createJobPosting(userAuth.getUserId(),dto);
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


    // ===== FILTER =====
    @Operation(
            summary = "Lọc Job Posting theo địa chỉ và lĩnh vực công việc dành cho trang DANH SÁCH VIỆC LÀM"
    )
    @GetMapping("/filter")
    public ResponseEntity<Response> filterJobPostings(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String jobField,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = jobPostingService.filterJobPostings(title, companyName, jobField, location, page, size);
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
    // ===== APPROVE =====
    @Operation(
            summary = "Duyệt tin tuyển dụng",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Duyệt thành công",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Job posting approved successfully\",\n" +
                                                    "  \"jobPostingDto\": {\n" +
                                                    "    \"id\": 1,\n" +
                                                    "    \"title\": \"Java Backend Developer\",\n" +
                                                    "    \"status\": \"ACTIVE\"\n" +
                                                    "  }\n" +"}"
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> approveJobPosting(@PathVariable Long id) {
        Response response = jobPostingService.approveJobPosting(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    // ===== LOCK =====
    @Operation(
            summary = "Khóa tin tuyển dụng",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Khóa thành công",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Job posting locked successfully\",\n" +
                                                    "  \"jobPostingDto\": {\n" +
                                                    "    \"id\": 1,\n" +
                                                    "    \"title\": \"Java Backend Developer\",\n" +
                                                    "    \"status\": \"LOCKED\"\n" +"  }\n" +"}"
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{id}/lock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYER')")
    public ResponseEntity<Response> lockJobPosting(@PathVariable Long id) {
        Response response = jobPostingService.lockJobPosting(id);
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
    @PreAuthorize("hasRole('EMPLOYER')")
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
            description = "Sử dụng token được tạo từ /api/job-postings/{jobId}/share",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lấy tin tuyển dụng thành công",
                            content = @Content(
                                    schema = @Schema(implementation = Response.class),
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Job posting retrieved successfully from share token\",\n" +
                                                    "  \"jobPostingDto\": {\n" +
                                                    "    \"id\": 1,\n" +
                                                    "    \"title\": \"Java Backend Developer\",\n" +
                                                    "    \"description\": \"Phát triển dịch vụ RESTful API cho hệ thống HRM\",\n" +
                                                    "    \"address\": \"Hà Nội\",\n" +
                                                    "    \"jobField\": \"Software Engineering\",\n" +
                                                    "    \"salary\": \"1200-1500 USD\",\n" +
                                                    "    \"status\": \"ACTIVE\",\n" +
                                                    "    \"createdAt\": \"2025-09-22T08:00:00\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/public/share-token")
    public ResponseEntity<Response> getJobPostingByShareToken(@RequestParam String token) {
        Response response = jobPostingService.getJobPostingByShareToken(token);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET BY ADDRESS =====
    @Operation(
            summary = "Lấy danh sách tin tuyển dụng theo địa chỉ",
            description = "Lấy danh sách job postings theo địa chỉ (phân trang, mặc định sort mới nhất)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Danh sách job postings",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Lấy danh sách tin tuyển dụng theo địa chỉ thành công\",\n" +
                                                    "  \"jobPostingDtoList\": [\n" +
                                                    "    {\n" +
                                                    "      \"id\": 5,\n" +
                                                    "      \"title\": \"Java Backend Developer\",\n" +
                                                    "      \"address\": \"Hà Nội\",\n" +
                                                    "      \"jobField\": \"Software Engineering\",\n" +
                                                    "      \"salary\": \"1200-1500 USD\",\n" +
                                                    "      \"createdAt\": \"2025-09-22T08:00:00\"\n" +
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
    @GetMapping("/by-address")
    public ResponseEntity<Response> getJobPostingsByAddress(
            @RequestParam(required = false) String address,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = jobPostingService.getJobPostingsByAddress(address, page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    // ===== GET CANDIDATES FOR JOB POSTING =====
    @Operation(
            summary = "Lấy danh sách ứng viên cho một Job Posting",
            description = "Trả về danh sách ứng viên ứng tuyển vào một tin tuyển dụng, có hỗ trợ phân trang, lọc theo status, sắp xếp theo ngày mới nhất/cũ nhất",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Danh sách ứng viên",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Lấy danh sách ứng viên ứng tuyển thành công\",\n" +
                                                    "  \"jobApplicationDtoList\": [\n" +
                                                    "    {\n" +
                                                    "      \"id\": 10,\n" +
                                                    "      \"candidate\": {\n" +
                                                    "        \"id\": 5,\n" +
                                                    "        \"fullName\": \"Nguyễn Văn A\",\n" +
                                                    "        \"email\": \"a@example.com\",\n" +
                                                    "        \"phone\": \"0123456789\"\n" +
                                                    "      },\n" +
                                                    "      \"status\": \"APPLIED\",\n" +
                                                    "      \"appliedAt\": \"2025-09-24T10:00:00\"\n" +
                                                    "    }\n" +
                                                    "  ],\n" +
                                                    "  \"currentPage\": 0,\n" +
                                                    "  \"totalItems\": 1,\n" +
                                                    "  \"totalPages\": 1\n" +
                                                    "}"
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{jobId}/candidates")
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getCandidatesForJobPosting(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status
    ) {
        Response response = jobPostingService.getCandidatesForJobPosting(jobId, page, size, sortDir, status);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Lọc tin tuyển dụng theo nhiều tiêu chí: tiêu đề, tên công ty, địa chỉ, mức lương dành cho trang chủ")
    @GetMapping("/list-filter")
    public ResponseEntity<Response> filterJobPostings(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Response response = jobPostingService.filterJobPostings(title, companyName, address, minSalary, maxSalary, page, size);
        return ResponseEntity.ok(response);
    }
}
