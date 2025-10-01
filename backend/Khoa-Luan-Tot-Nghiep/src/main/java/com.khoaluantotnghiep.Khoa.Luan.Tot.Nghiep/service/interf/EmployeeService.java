package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.EmployeeDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.EmployeeInfoCompanyRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
    Response getEmployeeByUserId(Long userId);
    Response createEmployee(EmployeeDto employeeDto);
    Response updateEmployeeInfoCompany(Long userId, EmployeeInfoCompanyRequest employeeInfoCompanyRequest);
    Response uploadAvatar(Long userId, MultipartFile avatarUrl);
    Response uploadCompanyLogo(Long userId, MultipartFile companyLogoUrl);
    Response uploadBusinessLicense(Long userId, MultipartFile businessLicenseUrl);
    Response getAllEmployees(int page, int size, String sortBy, String sortDir, String search);
    Response deleteEmployee(Long userId);
    Response getJobPostingsByEmployeeId_Status(Long userId, int page, int size, String status);
}
