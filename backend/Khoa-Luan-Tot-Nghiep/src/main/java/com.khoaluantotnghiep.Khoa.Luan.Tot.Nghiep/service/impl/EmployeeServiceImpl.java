package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.EmployeeDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.EmployeeInfoCompanyRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Employee;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobPostingStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.EmployeeMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.JobPostingMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.EmployeeRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.JobPostingRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.UserRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.CloudinaryService;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final JobPostingRepo jobPostingRepo;
    private final JobPostingMapper jobPostingMapper;
    private final UserRepo userRepo;
    private final EmployeeMapper employeeMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    public Response createEmployee(EmployeeDto employeeDto) {
        Employee employee = employeeMapper.toEntity(employeeDto);
        attachUserToEmployee(employee, employeeDto.getUserId());
        Employee savedEmployee = employeeRepo.save(employee);
        EmployeeDto savedEmployeeDto = employeeMapper.toDto(savedEmployee);

        return Response.builder()
                .status(201)
                .message("Employee created successfully")
                .employerDto(savedEmployeeDto)
                .build();
    }

    @Override
    public Response updateEmployeeInfoCompany(Long userId, EmployeeInfoCompanyRequest employeeInfoCompanyRequest) {
        Employee employee = employeeRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with userId: " + userId));

        employee.setCompanyName(employeeInfoCompanyRequest.getCompanyName());
        employee.setAddress(employeeInfoCompanyRequest.getAddress());
        employee.setScale(employeeInfoCompanyRequest.getScale());
        employee.setWebsite(employeeInfoCompanyRequest.getWebsite());
        employee.setDescription(employeeInfoCompanyRequest.getDescription());

        Employee updatedEmployee = employeeRepo.save(employee);
        EmployeeDto updatedEmployeeDto = employeeMapper.toDto(updatedEmployee);

        return Response.builder()
                .status(200)
                .message("Employee company info updated successfully")
                .employerDto(updatedEmployeeDto)
                .build();
    }

    @Override
    public Response uploadAvatar(Long userId, MultipartFile avatarUrl) {
        Employee employee = employeeRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with userId: " + userId));

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            String uploadedUrl = cloudinaryService.uploadImage(avatarUrl);
            employee.setAvatar(uploadedUrl);
        }
        Employee updatedEmployee = employeeRepo.save(employee);
        EmployeeDto updatedEmployeeDto = employeeMapper.toDto(updatedEmployee);

        return Response.builder()
                .status(200)
                .message("Avatar uploaded successfully")
                .employerDto(updatedEmployeeDto)
                .build();
    }

    @Override
    public Response uploadCompanyLogo(Long userId, MultipartFile companyLogoUrl) {
        Employee employee = employeeRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with userId: " + userId));

        if (companyLogoUrl != null && !companyLogoUrl.isEmpty()) {
            String uploadedUrl = cloudinaryService.uploadImage(companyLogoUrl);
            employee.setCompanyLogo(uploadedUrl);
        }
        Employee updatedEmployee = employeeRepo.save(employee);
        EmployeeDto updatedEmployeeDto = employeeMapper.toDto(updatedEmployee);

        return Response.builder()
                .status(200)
                .message("Logo Company uploaded successfully")
                .employerDto(updatedEmployeeDto)
                .build();
    }

    @Override
    public Response uploadBusinessLicense(Long userId, MultipartFile businessLicenseUrl) {
        Employee employee = employeeRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with userId: " + userId));

        if (businessLicenseUrl != null && !businessLicenseUrl.isEmpty()) {
            String uploadedUrl = cloudinaryService.uploadPdf(businessLicenseUrl);
            employee.setBusinessLicense(uploadedUrl);
        }
        Employee updatedEmployee = employeeRepo.save(employee);
        EmployeeDto updatedEmployeeDto = employeeMapper.toDto(updatedEmployee);

        return Response.builder()
                .status(200)
                .message("Business License uploaded successfully")
                .employerDto(updatedEmployeeDto)
                .build();
    }

    @Override
    public Response getEmployeeByUserId(Long userId) {
        Employee employee = employeeRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with userId: " + userId));
        if (employee == null) {
            throw new ResourceNotFoundException("Không tìm thấy nhà tuyển dụng!");
        }
        EmployeeDto employeeDto = employeeMapper.toDto(employee);
        return Response.builder()
                .status(200)
                .message("Employee retrieved successfully")
                .employerDto(employeeDto)
                .build();
    }

    @Override
    public Response getAllEmployees(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Employee> employeePage;
        if (search != null && !search.trim().isEmpty()) {
            employeePage = employeeRepo.findByCompanyNameContainingIgnoreCase(search.trim(), pageable);

            if (employeePage.isEmpty()) {
                employeePage = employeeRepo.findByUser_FullNameContainingIgnoreCase(search.trim(), pageable);
            }
        } else {
            employeePage = employeeRepo.findAll(pageable);
        }
        List<EmployeeDto> employeeDtos = employeePage.getContent()
                .stream()
                .map(employeeMapper::toDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Employees retrieved successfully")
                .employerDtoList(employeeDtos)
                .currentPage(employeePage.getNumber())
                .totalItems(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .build();
    }

    @Override
    public Response deleteEmployee(Long userId) {
        Employee employee = employeeRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with userId: " + userId));
        if(employee == null){
            throw new ResourceNotFoundException("Không tìm thấy nhà tuyển dụng!");
        }
        employeeRepo.delete(employee);
        return Response.builder()
                .status(200)
                .message("Employee deleted successfully")
                .build();
    }

    @Override
    public Response getJobPostingsByEmployeeId_Status(Long userId, int page, int size, String status) {
        Employee employee = employeeRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà tuyển dụng! "));
        if (employee == null) {
            throw new ResourceNotFoundException("Không tìm thấy nhà tuyển dụng!");
        }

        JobPostingStatus jobStatus;
        try {
            jobStatus = JobPostingStatus.valueOf(status.toUpperCase()); // convert String -> Enum
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Trạng thái không hợp lệ: " + status);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobPosting> jobPostingsPage = jobPostingRepo.findByEmployeeAndStatus(employee, jobStatus, pageable);

        if (jobPostingsPage.isEmpty()) {
            throw new ResourceNotFoundException("Không có tin tuyển dụng nào với trạng thái: " + status);
        }

        List<JobPostingDto> jobPostings = jobPostingsPage.getContent()
                .stream()
                .map(jobPostingMapper::toDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("Lấy tin tuyển dụng theo trạng thái thành công!")
                .jobPostingDtoList(jobPostings)
                .currentPage(jobPostingsPage.getNumber())
                .totalItems(jobPostingsPage.getTotalElements())
                .totalPages(jobPostingsPage.getTotalPages())
                .build();
    }

    private void attachUserToEmployee(Employee employee, Long userId) {
        if (userId != null) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            employee.setUser(user);
        }
    }
}
