package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.ExportReportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ExportReportServiceImpl implements ExportReportService {

    @Override
    public ResponseEntity<byte[]> exportJobPostingsToExcel(List<JobPostingDto> postings) {
        if (postings == null || postings.isEmpty()) {
            throw new ResourceNotFoundException("Không có tin tuyển dụng nào để export");
        }
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Job Postings");

            // Header
            String[] headers = {"ID", "Tiêu đề", "Công ty", "Địa chỉ", "Mức lương tối thiểu", "Mức lương tối đa", "Trạng thái", "Ngày tạo"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Data
            int rowIdx = 1;
            for (JobPostingDto posting : postings) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(posting.getJobId());
                row.createCell(1).setCellValue(posting.getTitle());
                row.createCell(2).setCellValue(posting.getEmployee().getCompanyName());
                row.createCell(3).setCellValue(posting.getAddress());
                row.createCell(4).setCellValue(posting.getSalaryMin() != null ? posting.getSalaryMin().toString() : "");
                row.createCell(5).setCellValue(posting.getSalaryMax() != null ? posting.getSalaryMax().toString() : "");
                row.createCell(6).setCellValue(posting.getStatus());
                row.createCell(7).setCellValue(posting.getCreatedAt() != null ? posting.getCreatedAt().toString() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            HttpHeaders headersExcel = new HttpHeaders();
            headersExcel.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headersExcel.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=job_postings.xlsx");

            return new ResponseEntity<>(out.toByteArray(), headersExcel, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi export Excel: " + e.getMessage());
        }
    }
}
