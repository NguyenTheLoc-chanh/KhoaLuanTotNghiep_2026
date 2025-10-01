package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.JobPostingMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.JobPostingRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.ExportReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/export-report")
@RequiredArgsConstructor
@Tag(name = "Download Excel", description = "API quản lý xuất file excel")
public class ExportReportController {
    private final ExportReportService exportReportService;
    private final JobPostingRepo jobPostingRepo;
    private final JobPostingMapper jobPostingMapper;

    @Operation(summary = "Export danh sách tin tuyển dụng đã chọn ra Excel")
    @PostMapping("/export/excel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<byte[]> exportSelectedJobPostings(@RequestBody List<Long> jobIds) {
        List<JobPosting> postings = jobPostingRepo.findAllById(jobIds);

        if (postings.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy tin tuyển dụng nào");
        }
        // Map sang DTO
        List<JobPostingDto> postingDtos = postings.stream()
                .map(jobPostingMapper::toDto)
                .toList();
        return exportReportService.exportJobPostingsToExcel(postingDtos);
    }

}
