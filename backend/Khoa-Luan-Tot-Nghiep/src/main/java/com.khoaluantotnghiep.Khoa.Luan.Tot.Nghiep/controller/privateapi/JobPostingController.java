package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller.privateapi;

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
@RequestMapping("/api/private/job-postings")
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
}
