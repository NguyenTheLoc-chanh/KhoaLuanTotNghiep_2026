package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics - ADMIN", description = "API thống kê cho hệ thống tuyển dụng")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/jobs")
    @Operation(
            summary = "Thống kê bài đăng việc làm",
            description = "Trả về số lượng bài đăng việc làm theo trạng thái (ACTIVE, PENDING, LOCKED, EXPIRED)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Thành công",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Response jobPostingStatistics() {
        return statisticsService.jobPostingStatistics();
    }

    @GetMapping("/employees")
    @Operation(
            summary = "Thống kê nhân viên",
            description = "Trả về tổng số nhân viên",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Thành công",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Response employeeStatistics() {
        return statisticsService.employeeStatistics();
    }

    @GetMapping("/candidates")
    @Operation(
            summary = "Thống kê ứng viên",
            description = "Trả về tổng số ứng viên",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Thành công",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Response candidateStatistics() {
        return statisticsService.candidateStatistics();
    }

    @GetMapping("/feedbacks")
    @Operation(
            summary = "Thống kê feedback",
            description = "Trả về số lượng feedback theo trạng thái (PENDING, RESOLVED)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Thành công",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Response feedbackStatistics() {
        return statisticsService.feedbackStatistics();
    }
    @Operation(
            summary = "Thống kê mẫu CV",
            description = "Trả về tổng số mẫu CV",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Thành công",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/sample-cvs")
    @PreAuthorize("hasRole('ADMIN')")
    public Response sampleCVStatistics() {
        return statisticsService.sampleCVStatistics();
    }
}
