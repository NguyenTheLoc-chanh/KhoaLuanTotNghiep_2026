package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ExportReportService {
    ResponseEntity<byte[]> exportJobPostingsToExcel(List<JobPostingDto> postings);
    ResponseEntity<byte[]> exportCandidateToExcel(List<CandidateDto> candidates);
    ResponseEntity<byte[]> exportEmployeeToExcel(List<EmployeeDto> employees);
    ResponseEntity<byte[]> exportFeedbackToExcel(List<FeedbackDto> feedbacks);
    ResponseEntity<byte[]> exportSampleCVToExcel(List<SampleCVDto> sampleCVs);
}
