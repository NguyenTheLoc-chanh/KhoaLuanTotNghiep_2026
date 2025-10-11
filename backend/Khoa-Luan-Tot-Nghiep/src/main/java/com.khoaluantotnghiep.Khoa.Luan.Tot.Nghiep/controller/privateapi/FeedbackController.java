package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller.privateapi;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.FeedbackRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.FeedbackReplyRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.UserAuth;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.FeedbackService;
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
@RequestMapping("/api/private/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "API quản lý Feedback của người dùng")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(
            summary = "Tạo mới feedback",
            description = "Người dùng gửi feedback cho hệ thống",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FeedbackRequest.class),
                            examples = @ExampleObject(
                                    name = "FeedbackRequest",
                                    value = "{\n" +
                                            "  \"title\": \"Ứng dụng bị lỗi login\",\n" +
                                            "  \"description\": \"Nhập mật khẩu đúng nhưng báo sai\"\n" +
                                            "}"
                            )
                    )
            )
    )
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response> createFeedback(
            @AuthenticationPrincipal UserAuth userAuth,
            @org.springframework.web.bind.annotation.RequestBody FeedbackRequest request
    ) {
        Long userId = userAuth.getUserId();
        Response response = feedbackService.createFeedback(userId, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    // ===== GET ALL FEEDBACKS (PAGINATED) =====
    @Operation(
            summary = "Lấy danh sách tất cả feedback (có phân trang)",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Danh sách feedback",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"Lấy danh sách feedback thành công\",\n" +
                                            "  \"feedbackDtoList\": [\n" +
                                            "    {\n" +
                                            "      \"feedbackId\": 1,\n" +
                                            "      \"title\": \"Ứng dụng bị lỗi login\",\n" +
                                            "      \"description\": \"Nhập mật khẩu đúng nhưng báo sai\",\n" +
                                            "      \"status\": \"PENDING\",\n" +
                                            "      \"userId\": 5\n" +
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> getAllFeedbacks(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Response response = feedbackService.getAllFeedbacks(page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET FEEDBACK BY ID =====
    @Operation(
            summary = "Xem chi tiết feedback theo ID",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Chi tiết feedback",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"Lấy feedback thành công\",\n" +
                                            "  \"feed\": {\n" +
                                            "    \"feedbackId\": 1,\n" +
                                            "    \"title\": \"Ứng dụng bị lỗi login\",\n" +
                                            "    \"description\": \"Nhập mật khẩu đúng nhưng báo sai\",\n" +
                                            "    \"status\": \"PENDING\",\n" +
                                            "    \"userId\": 5\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{feedbackId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> getFeedbackById(@PathVariable Long feedbackId) {
        Response response = feedbackService.getFeedbackById(feedbackId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== UPDATE FEEDBACK STATUS =====
    @Operation(
            summary = "Cập nhật trạng thái feedback",
            description = "Admin hoặc Employer có thể đổi trạng thái PENDING/RESOLVED",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật trạng thái thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"Cập nhật trạng thái feedback thành công\",\n" +
                                            "  \"feed\": {\n" +
                                            "    \"feedbackId\": 1,\n" +
                                            "    \"status\": \"RESOLVED\"\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{feedbackId}/status")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> updateFeedbackStatus(
            @PathVariable Long feedbackId,
            @RequestParam String status
    ) {
        Response response = feedbackService.updateFeedbackStatus(feedbackId, status);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== DELETE FEEDBACK =====
    @Operation(
            summary = "Xóa feedback theo ID",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Xóa thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"Xóa feedback thành công\"\n" +
                                            "}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteFeedback(@PathVariable Long feedbackId) {
        Response response = feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    // ===== REPLY TO FEEDBACK =====
    @Operation(
            summary = "Phản hồi feedback của người dùng",
            description = "Admin trả lời phản hồi của người dùng qua email và cập nhật trạng thái feedback thành RESOLVED",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"replyContent\": \"Cảm ơn bạn đã phản hồi. Vấn đề của bạn đã được xử lý.\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Phản hồi thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"Phản hồi feedback thành công\",\n" +
                                            "  \"feed\": {\n" +
                                            "    \"feedbackId\": 1,\n" +
                                            "    \"status\": \"RESOLVED\"\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{feedbackId}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> replyToFeedback(
            @PathVariable Long feedbackId,
            @org.springframework.web.bind.annotation.RequestBody FeedbackReplyRequest request
    ) {
        Response response = feedbackService.replyToFeedback(feedbackId, request.getReplyContent());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET FEEDBACKS BY USER ID (PAGINATED) =====
    @Operation(
            summary = "Lấy danh sách feedback của 1 user (có phân trang)",
            description = "User xem danh sách feedback của chính mình",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Danh sách feedback theo userId",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"Lấy danh sách feedback thành công\",\n" +
                                            "  \"feedbackDtoList\": [\n" +
                                            "    {\n" +
                                            "      \"feedbackId\": 1,\n" +
                                            "      \"title\": \"Ứng dụng bị lỗi login\",\n" +
                                            "      \"description\": \"Nhập mật khẩu đúng nhưng báo sai\",\n" +
                                            "      \"status\": \"PENDING\",\n" +
                                            "      \"userId\": 5\n" +
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
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER','CANDIDATE')")
    public ResponseEntity<Response> getAllFeedbacksByUserId(
            @AuthenticationPrincipal UserAuth userAuth,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Response response = feedbackService.getAllFeedbacksByUserId(userAuth.getUserId(), page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
