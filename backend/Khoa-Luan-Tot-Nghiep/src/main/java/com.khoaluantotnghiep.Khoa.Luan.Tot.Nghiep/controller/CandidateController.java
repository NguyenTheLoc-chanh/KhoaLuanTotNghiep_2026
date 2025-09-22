package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.CandidateDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@Tag(name = "Candidate", description = "API quản lý hồ sơ ứng viên")
public class CandidateController {

    private final CandidateService candidateService;
    private final ObjectMapper objectMapper;

    // ===== CREATE =====
    @Operation(
            summary = "Tạo hồ sơ ứng viên",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tạo thành công",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(example = "{\n" +
                                            "  \"status\": 201,\n" +
                                            "  \"message\": \"Candidate created successfully\",\n" +
                                            "  \"candidateDto\": {\n" +
                                            "    \"candidateId\": 1,\n" +
                                            "    \"avatar\": \"https://...\",\n" +
                                            "    \"experienceYear\": 3,\n" +
                                            "    \"userId\": 10\n" +
                                            "  }\n" +
                                            "}"))),
                    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Response> createCandidate(@Valid @RequestBody CandidateDto candidateDto) {
        Response response = candidateService.createCandidate(candidateDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET BY USER ID =====
    @Operation(
            summary = "Lấy hồ sơ ứng viên theo userId",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE','ADMIN')")
    public ResponseEntity<Response> getCandidateByUserId(@PathVariable Long userId) {
        Response response = candidateService.getCandidateByUserId(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Cập nhật thông tin ứng viên (không bao gồm avatar)")
    @PutMapping("/{userId}/info")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Response> updateCandidateInfo(
            @PathVariable Long userId,
            @Valid @RequestBody CandidateDto candidateDto
    ) {
        candidateDto.setUserId(userId);
        Response response = candidateService.updateCandidateInfo(candidateDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ==== Update candidate avatar ====
    @Operation(summary = "Cập nhật avatar ứng viên")
    @PutMapping(value = "/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Response> updateCandidateAvatar(
            @PathVariable Long userId,
            @RequestPart("avatarFile") MultipartFile avatarFile
    ) {
        Response response = candidateService.updateCandidateAvatar(userId, avatarFile);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Cập nhật File CV ứng viên")
    @PutMapping(value = "/{userId}/fileCV", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Response> updateCandidateFileCV(
            @PathVariable Long userId,
            @RequestPart("fCvFile") MultipartFile fCvFile
    ) {
        Response response = candidateService.updateCandidateFCv(userId, fCvFile);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== DELETE =====
    @Operation(
            summary = "Xóa hồ sơ ứng viên theo userId",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Xóa thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteCandidate(@PathVariable Long userId) {
        Response response = candidateService.deleteCandidate(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ===== GET ALL (PAGINATION) =====
    @Operation(
            summary = "Lấy danh sách ứng viên (có phân trang, tìm kiếm, sắp xếp)",
            description = "Truyền query param: page, size, sortBy, sortDir, search",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Thành công")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER','CANDIDATE')")
    public ResponseEntity<Response> getAllCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "candidateId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search
    ) {
        Response response = candidateService.getAllCandidates(page, size, sortBy, sortDir, search);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
