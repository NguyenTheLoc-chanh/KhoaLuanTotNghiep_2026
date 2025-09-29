package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-applications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Job Application", description = "API quản lý ứng tuyển")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @Operation(
            summary = "Lấy danh sách tất cả ứng tuyển",
            description = "Trả về danh sách các ứng tuyển, có phân trang, sort và search theo fullName",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
                            content = @Content(schema = @Schema(implementation = Response.class))),
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> getAllJobApplications(
            @Parameter(description = "Số trang (mặc định 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang (mặc định 10)") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Trường sort (mặc định appliedAt)") @RequestParam(defaultValue = "appliedAt") String sortBy,
            @Parameter(description = "Hướng sort (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Từ khóa tìm kiếm theo fullName") @RequestParam(required = false) String search
    ) {
        Response response = jobApplicationService.getAllJobApplications(page, size, sortBy, sortDir, search);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Lấy thông tin ứng tuyển theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lấy thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy ứng tuyển")
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> getJobApplicationById(
            @Parameter(description = "ID ứng tuyển") @PathVariable Long id
    ) {
        Response response = jobApplicationService.getJobApplicationById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Duyệt ứng tuyển",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Duyệt thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy ứng tuyển")
            }
    )
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> approveJobApplication(
            @Parameter(description = "ID ứng tuyển cần duyệt") @PathVariable Long id
    ) {
        Response response = jobApplicationService.approveJobApplication(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Từ chối ứng tuyển",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Từ chối thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy ứng tuyển")
            }
    )
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> rejectJobApplication(
            @Parameter(description = "ID ứng tuyển cần từ chối") @PathVariable Long id
    ) {
        Response response = jobApplicationService.rejectJobApplication(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Lọc ứng tuyển theo trạng thái",
            description = "Trạng thái có thể là: PENDING, VIEWED, APPROVED, REJECTED",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lọc thành công"),
                    @ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ")
            }
    )
    @GetMapping("/filter")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> filterJobApplicationsByStatus(
            @Parameter(description = "Trạng thái cần lọc (PENDING/VIEWED/APPROVED/REJECTED)") @RequestParam String status,
            @Parameter(description = "Số trang (mặc định 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang (mặc định 10)") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Trường sort (mặc định appliedAt)") @RequestParam(defaultValue = "appliedAt") String sortBy,
            @Parameter(description = "Hướng sort (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Response response = jobApplicationService.filterJobApplicationsByStatus(status, page, size, sortBy, sortDir);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Lấy danh sách ứng tuyển theo JobId",
            description = "Trả về danh sách ứng tuyển của một tin tuyển dụng (jobId), có phân trang và sort",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy ứng tuyển cho jobId này")
            }
    )
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> getJobApplicationsByJobId(
            @Parameter(description = "ID tin tuyển dụng") @PathVariable Long jobId,
            @Parameter(description = "Số trang (mặc định 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang (mặc định 10)") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Trường sort (mặc định appliedAt)") @RequestParam(defaultValue = "appliedAt") String sortBy,
            @Parameter(description = "Hướng sort (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Response response = jobApplicationService.getJobApplicationsByJobId(jobId, page, size, sortBy, sortDir);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Gửi thư mời phỏng vấn",
            description = "HR có thể nhập nội dung thư, dùng placeholder ${name} để hệ thống tự thay bằng tên ứng viên",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Gửi thư thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy ứng tuyển")
            }
    )
    @PostMapping("/{id}/send-interview-letter")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> sendInterviewLetter(
            @Parameter(description = "ID ứng tuyển cần gửi thư") @PathVariable Long id,
            @RequestBody String request
    ) {
        Response response = jobApplicationService.sendInterviewLatter(id, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
