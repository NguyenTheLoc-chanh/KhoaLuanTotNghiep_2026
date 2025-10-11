package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller.privateapi;

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
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/private/job-categories")
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
}
