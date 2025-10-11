package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller.privateapi;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.CandidateDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.*;
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
@RequestMapping("/api/private/export-report")
@RequiredArgsConstructor
@Tag(name = "Download Excel", description = "API quản lý xuất file excel")
public class ExportReportController {
    private final ExportReportService exportReportService;
    private final JobPostingRepo jobPostingRepo;
    private final JobPostingMapper jobPostingMapper;
    private final CandidateRepo candidateRepo;
    private final CandidateMapper candidateMapper;
    private final EmployeeRepo employeeRepo;
    private final EmployeeMapper employeeMapper;
    private final FeedbackRepo feedbackRepo;
    private final FeedbackMapper feedbackMapper;
    private final SampleCVRepo sampleCVRepo;
    private final SampleCVMapper sampleCVMapper;

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

    @Operation(summary = "Export danh sách ứng viên đã chọn ra Excel")
    @PostMapping("/export/candidate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<byte[]> exportSelectedCandidates(@RequestBody List<Long> candidateIds) {
        List<Candidate> candidates = candidateRepo.findAllById(candidateIds);
        if (candidates.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy ứng viên nào");
        }
        // Map sang DTO
        List<CandidateDto> candidateDtos = candidates.stream()
                .map(candidateMapper::toDto)
                .toList();
        return exportReportService.exportCandidateToExcel(candidateDtos);
    }

    @Operation(summary = "Export danh sách nhân viên đã chọn ra Excel")
    @PostMapping("/export/employee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportSelectedEmployees(@RequestBody List<Long> employeeIds) {
        List<Employee> employees = employeeRepo.findAllById(employeeIds);
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy nhân viên nào");

        }
        // Map sang DTO
        var employeeDtos = employees.stream()
                .map(employeeMapper::toDto)
                .toList();
        return exportReportService.exportEmployeeToExcel(employeeDtos);
    }

    @Operation(summary = "Export danh sách phản hồi đã chọn ra Excel")
    @PostMapping("/export/feedback")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<byte[]> exportSelectedFeedbacks(@RequestBody List<Long> feedbackIds) {
        List<Feedback> feedbacks = feedbackRepo.findAllById(feedbackIds);
        if (feedbacks.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy phản hồi nào");
        }
        // Map sang DTO
        var feedbackDtos = feedbacks.stream()
                .map(feedbackMapper::toDto)
                .toList();
        return exportReportService.exportFeedbackToExcel(feedbackDtos);
    }

    @Operation(summary = "Export danh sách mẫu CV đã chọn ra Excel")
    @PostMapping("/export/sample-cv")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<byte[]> exportSelectedSampleCVs(@RequestBody List<Long> sampleCVIds) {
        List<SampleCV> sampleCVs = sampleCVRepo.findAllById(sampleCVIds);
        if (sampleCVs.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy mẫu CV nào");
        }
        // Map sang DTO
        var sampleCVDtos = sampleCVs.stream()
                .map(sampleCVMapper::toDto)
                .toList();
        return exportReportService.exportSampleCVToExcel(sampleCVDtos);
    }
}
