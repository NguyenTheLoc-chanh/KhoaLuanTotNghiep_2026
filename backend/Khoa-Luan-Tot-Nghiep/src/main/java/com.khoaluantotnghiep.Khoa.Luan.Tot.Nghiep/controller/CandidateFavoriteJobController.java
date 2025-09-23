package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.CandidateFavoriteJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidates/{candidateId}/favorites")
@RequiredArgsConstructor
@Tag(name = "Candidate Favorite Jobs", description = "API quản lý công việc yêu thích của ứng viên")
public class CandidateFavoriteJobController {

    private final CandidateFavoriteJobService candidateFavoriteJobService;

    // ===== ADD JOB TO FAVORITES =====
    @Operation(
            summary = "Ứng viên thêm công việc vào danh sách yêu thích",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Thêm công việc vào yêu thích thành công",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 201,\n" +
                                                    "  \"message\": \"Job added to favorites successfully\",\n" +
                                                    "  \"candidateFavoriteJobDto\": {\n" +
                                                    "    \"id\": 1,\n" +
                                                    "    \"candidateId\": 5,\n" +
                                                    "    \"jobId\": 10\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Công việc đã tồn tại trong danh sách yêu thích"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy ứng viên hoặc công việc")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{jobId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Response> addFavoriteJob(@PathVariable Long candidateId, @PathVariable Long jobId) {
        Response response = candidateFavoriteJobService.addFavoriteJob(candidateId, jobId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== REMOVE JOB FROM FAVORITES =====
    @Operation(
            summary = "Ứng viên xóa công việc khỏi danh sách yêu thích",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Xóa công việc khỏi yêu thích thành công",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Job removed from favorites successfully\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy trong danh sách yêu thích")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Response> removeFavoriteJob(@PathVariable Long candidateId, @PathVariable Long jobId) {
        Response response = candidateFavoriteJobService.removeFavoriteJob(candidateId, jobId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET ALL FAVORITES =====
    @Operation(
            summary = "Lấy danh sách công việc yêu thích của ứng viên",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lấy danh sách thành công",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            example = "{\n" +
                                                    "  \"status\": 200,\n" +
                                                    "  \"message\": \"Fetched favorite jobs successfully\",\n" +
                                                    "  \"candidateFavoriteJobDtoList\": [\n" +
                                                    "    {\"id\": 1, \"candidateId\": 5, \"jobId\": 10},\n" +
                                                    "    {\"id\": 2, \"candidateId\": 5, \"jobId\": 12}\n" +
                                                    "  ]\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy ứng viên")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Response> getFavoriteJobs(@PathVariable Long candidateId) {
        Response response = candidateFavoriteJobService.getFavoriteJobs(candidateId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
