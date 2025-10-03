package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.*;
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

        String[] headers = {"ID", "Tiêu đề", "Công ty", "Địa chỉ", "Mức lương tối thiểu", "Mức lương tối đa", "Trạng thái", "Ngày tạo"};

        List<List<String>> rows = postings.stream().map(p -> List.of(
                String.valueOf(p.getJobId()),
                p.getTitle(),
                p.getEmployee() != null ? p.getEmployee().getCompanyName() : "",
                p.getAddress(),
                p.getSalaryMin() != null ? p.getSalaryMin().toString() : "",
                p.getSalaryMax() != null ? p.getSalaryMax().toString() : "",
                p.getStatus() != null ? translateJobStatus(p.getStatus()) : "",
                p.getCreatedAt() != null ? p.getCreatedAt().toString() : ""
        )).toList();

        return buildExcelResponse("Job Postings", "job_postings.xlsx", headers, rows);
    }

    private String translateJobStatus(String status) {
        return switch (status) {
            case "PENDING" -> "Đang chờ duyệt";
            case "ACTIVE" -> "Đã duyệt";
            case "EXPIRED" -> "Hết hạn";
            case "LOCKED" -> "Đã khóa";
            default -> "Không xác định";
        };
    }


    @Override
    public ResponseEntity<byte[]> exportCandidateToExcel(List<CandidateDto> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            throw new ResourceNotFoundException("Không có ứng viên nào để export");
        }

        String[] headers = {"ID", "Họ và tên", "Avatar", "Email", "Số điện thoại", "Số năm kinh nghiệm", "Trạng thái", "Ngày tạo"};

        List<List<String>> rows = candidates.stream().map(c -> List.of(
                String.valueOf(c.getCandidateId()),
                c.getFullName() != null ? c.getFullName() : "",
                c.getAvatar() != null ? c.getAvatar() : "",
                c.getEmail() != null ? c.getEmail() : "",
                c.getPhone() != null ? c.getPhone() : "",
                c.getExperienceYear() != null ? c.getExperienceYear().toString() : "",
                translateUserStatus(c.getStatus()),
                c.getCreatedAt() != null ? c.getCreatedAt().toString() : ""
        )).toList();
        return buildExcelResponse("Candidates", "candidates.xlsx", headers, rows);
    }

    private String translateUserStatus(String status) {
        return switch (status) {
            case "ACTIVE" -> "Đang hoạt động";
            case "LOCKED" -> "Đã khóa";
            default -> "Không xác định";
        };
    }


    @Override
    public ResponseEntity<byte[]> exportEmployeeToExcel(List<EmployeeDto> employees) {
        if (employees == null || employees.isEmpty()) {
            throw new ResourceNotFoundException("Không có nhà tuyển dụng nào để export");
        }

        String[] headers = {"ID", "Họ và tên", "Avatar", "Địa chỉ", "Tên công ty", "Quy mô công ty", "Website", "Giấy phép kinh doanh", "Trạng thái", "Ngày tạo"};

        List<List<String>> rows = employees.stream().map(c -> List.of(
                String.valueOf(c.getEmployeeId()),
                c.getFullName() != null ? c.getFullName() : "",
                c.getAvatar() != null ? c.getAvatar() : "",
                c.getAddress() != null ? c.getAddress() : "",
                c.getCompanyName() != null ? c.getCompanyName() : "",
                c.getScale() != null ? c.getScale() : "",
                c.getWebsite() != null ? c.getWebsite() : "",
                c.getBusinessLicense() != null ? c.getBusinessLicense() : "",
                translateUserStatus(c.getStatus()),
                c.getCreatedAt() != null ? c.getCreatedAt().toString() : ""
        )).toList();


        return buildExcelResponse("Employees", "employees.xlsx", headers, rows);
    }

    @Override
    public ResponseEntity<byte[]> exportFeedbackToExcel(List<FeedbackDto> feedbacks) {
        if (feedbacks == null || feedbacks.isEmpty()) {
            throw new ResourceNotFoundException("Không có phản hồi nào để export");
        }
        String[] headers = {"ID", "Tiêu đề", "Mô tả", "Trạng thái", "Email", "Ngày tạo"};
        List<List<String>> rows = feedbacks.stream().map(f -> List.of(
                String.valueOf(f.getFeedbackId()),
                f.getTitle() != null ? f.getTitle() : "",
                f.getDescription() != null ? f.getDescription() : "",
                translateFeedbackStatus(f.getStatus()),
                f.getEmail() != null ? f.getEmail() : "",
                f.getCreatedAt() != null ? f.getCreatedAt().toString() : ""
        )).toList();
        return buildExcelResponse("Feedbacks", "feedbacks.xlsx", headers, rows);
    }
    private String translateFeedbackStatus(String status) {
        return switch (status) {
            case "PENDING" -> "Chưa xử lý";
            case "RESOLVED" -> "Đã xử lý";
            default -> "Không xác định";
        };
    }

    @Override
    public ResponseEntity<byte[]> exportSampleCVToExcel(List<SampleCVDto> sampleCVs) {
        if (sampleCVs == null || sampleCVs.isEmpty()) {
            throw new ResourceNotFoundException("Không có mẫu CV nào để export");
        }
        String[] headers = {"ID", "Tiêu đề", "Mô tả", "Link mẫu CV", "Ngày tạo"};
        List<List<String>> rows = sampleCVs.stream().map(cv -> List.of(
                String.valueOf(cv.getSampleCVId()),
                cv.getTitle() != null ? cv.getTitle() : "",
                cv.getDescription() != null ? cv.getDescription() : "",
                cv.getFCvFileFormat() != null ? cv.getFCvFileFormat() : "",
                cv.getCreatedAt() != null ? cv.getCreatedAt().toString() : ""
        )).toList();
        return buildExcelResponse("Sample CVs", "sample_cvs.xlsx", headers, rows);
    }

    private ResponseEntity<byte[]> buildExcelResponse(
            String sheetName,
            String fileName,
            String[] headers,
            List<List<String>> rows
    ) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Data rows
            int rowIdx = 1;
            for (List<String> rowData : rows) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < rowData.size(); i++) {
                    row.createCell(i).setCellValue(rowData.get(i) != null ? rowData.get(i) : "");
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            HttpHeaders headersExcel = new HttpHeaders();
            headersExcel.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headersExcel.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return new ResponseEntity<>(out.toByteArray(), headersExcel, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi export Excel: " + e.getMessage());
        }
    }

}
