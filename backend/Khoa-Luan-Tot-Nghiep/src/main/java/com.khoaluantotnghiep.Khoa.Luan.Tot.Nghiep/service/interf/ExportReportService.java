package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ExportReportService {
    ResponseEntity<byte[]> exportJobPostingsToExcel(List<JobPostingDto> postings);
}
