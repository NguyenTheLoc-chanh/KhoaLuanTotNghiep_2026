package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobApplicationDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.response.JobPostingCardDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Employee;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobApplication;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobCategory;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobApplicationStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobPostingStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.BadRequestException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.JobApplicationMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.JobPostingMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.EmployeeRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.JobApplicationRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.JobCategoryRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.JobPostingRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.JwtUtils;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.EmailService;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.JobPostingService;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.specification.JobPostingSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepo jobPostingRepo;
    private final JobCategoryRepo jobCategoryRepo;
    private final JobApplicationRepo jobApplicationRepo;
    private final EmployeeRepo employeeRepo;
    private final JobPostingMapper jobPostingMapper;
    private final JobApplicationMapper jobApplicationMapper;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    private void validateJobPostingDto(JobPostingDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new BadRequestException("Tiều đề công việc không được để trống");
        }
        if (dto.getEmployee().getEmployeeId() == null) {
            throw new BadRequestException("Nhà tuyển dụng không được để trống");
        }
    }

    private JobPosting buildJobPosting(JobPostingDto dto, Employee employee, JobCategory jobCategory) {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle(dto.getTitle());
        jobPosting.setJobField(dto.getJobField());
        jobPosting.setJobPosition(dto.getJobPosition());
        jobPosting.setAddress(dto.getAddress());
        jobPosting.setExperienceYear(dto.getExperienceYear());
        jobPosting.setQuantity(dto.getQuantity());
        jobPosting.setSalaryMin(dto.getSalaryMin());
        jobPosting.setSalaryMax(dto.getSalaryMax());
        jobPosting.setNegotiable(dto.getNegotiable());
        jobPosting.setEndDate(dto.getEndDate());
        jobPosting.setDescription(dto.getDescription());
        jobPosting.setStatus(JobPostingStatus.PENDING);
        jobPosting.setEmployee(employee);
        jobPosting.setJobCategory(jobCategory);
        return jobPosting;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void autoUpdateExpiredJobPostings() {
        jobPostingRepo.updateExpiredJobs(LocalDateTime.now());
    }

    private JobPosting checkAndUpdateExpiration(JobPosting posting) {
        if (posting.getEndDate() != null
                && posting.getEndDate().isBefore(LocalDateTime.now())
                && posting.getStatus() == JobPostingStatus.ACTIVE) {
            posting.setStatus(JobPostingStatus.EXPIRED);
            return jobPostingRepo.save(posting);
        }
        return posting;
    }


    @Override
    public Response createJobPosting(Long userId, JobPostingDto jobPostingDto) {
        validateJobPostingDto(jobPostingDto);
        Employee employee = employeeRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại nhà tuyển dụng"));

        JobCategory jobCategory = jobCategoryRepo.findById(jobPostingDto.getJobCategory().getJobCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục công việc" ));

        JobPosting jobPosting = buildJobPosting(jobPostingDto, employee, jobCategory);
        JobPosting saved = jobPostingRepo.save(jobPosting);
        JobPostingDto savedDto = jobPostingMapper.toDto(saved);

        return Response.builder()
                .status(201)
                .message("Tạo tin tuyển dụng thành công, vui lòng chờ xét duyệt từ Admin")
                .jobPostingDto(savedDto)
                .build();
    }

    @Override
    public Response getAllJobPostings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<JobPosting> postings = jobPostingRepo.findAll(pageable);

        List<JobPostingDto> jobPostingDtos = postings.getContent().stream()
                .map(this::checkAndUpdateExpiration)
                .map(jobPostingMapper::toDto)
                .toList();

        if (jobPostingDtos.isEmpty()) {
            throw new ResourceNotFoundException("Thông tin tuyển dụng trống");
        }

        return Response.builder()
                .status(200)
                .message("Lấy danh sách tin tuyển dụng thành công")
                .jobPostingDtoList(jobPostingDtos)
                .currentPage(postings.getNumber())
                .totalItems(postings.getTotalElements())
                .totalPages(postings.getTotalPages())
                .build();
    }

    @Override
    public Response getJobPostingById(Long id) {
        JobPosting posting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng"));

        posting = checkAndUpdateExpiration(posting);

        JobPostingDto postingDto = jobPostingMapper.toDto(posting);

        return Response.builder()
                .status(200)
                .message("Lấy tin tuyển dụng thành công")
                .jobPostingDto(postingDto)
                .build();
    }

    @Override
    public Response updateJobPosting(Long id, JobPostingDto jobPostingDto) {
        JobPosting posting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng"));

        if (jobPostingDto.getTitle() != null && !jobPostingDto.getTitle().isBlank())
            posting.setTitle(jobPostingDto.getTitle());
        if (jobPostingDto.getJobField() != null) posting.setJobField(jobPostingDto.getJobField());
        if (jobPostingDto.getJobPosition() != null) posting.setJobPosition(jobPostingDto.getJobPosition());
        if (jobPostingDto.getAddress() != null) posting.setAddress(jobPostingDto.getAddress());
        if (jobPostingDto.getExperienceYear() != null) posting.setExperienceYear(jobPostingDto.getExperienceYear());
        if (jobPostingDto.getQuantity() != null) posting.setQuantity(jobPostingDto.getQuantity());
        if (jobPostingDto.getSalaryMin() != null) posting.setSalaryMin(jobPostingDto.getSalaryMin());
        if (jobPostingDto.getSalaryMax() != null) posting.setSalaryMax(jobPostingDto.getSalaryMax());
        if (jobPostingDto.getNegotiable() != null) posting.setNegotiable(jobPostingDto.getNegotiable());
        if (jobPostingDto.getEndDate() != null) posting.setEndDate(jobPostingDto.getEndDate());
        if (jobPostingDto.getDescription() != null) posting.setDescription(jobPostingDto.getDescription());
        if (jobPostingDto.getStatus() != null) {
            try {
                posting.setStatus(JobPostingStatus.valueOf(jobPostingDto.getStatus().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid status: " + jobPostingDto.getStatus());
            }
        }

        JobPosting updated = jobPostingRepo.save(posting);
        JobPostingDto updatedDto = jobPostingMapper.toDto(updated);

        return Response.builder()
                .status(200)
                .message("Cập nhật tin tuyển dụng thành công")
                .jobPostingDto(updatedDto)
                .build();
    }

    @Override
    public Response deleteJobPosting(Long id) {
        JobPosting posting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng cần xóa"));

        jobPostingRepo.delete(posting);

        return Response.builder()
                .status(200)
                .message("Xóa tin tuyển dụng thành công")
                .build();
    }

    @Override
    public Response searchJobPostings(String keyword, int page, int size, String location) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<JobPosting> spec = (root, query, cb) -> cb.conjunction();

        // Trạng thái ACTIVE
        spec = spec.and(JobPostingSpecification.isActive());

        // Tìm theo tiêu đề công việc
        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(Specification.anyOf(
                    JobPostingSpecification.hasTitle(keyword),
                    JobPostingSpecification.hasCompanyName(keyword)
            ));
        }

        // Tìm theo địa điểm
        if (location != null && !location.isBlank()) {
            spec = spec.and(JobPostingSpecification.hasAddress(location));
        }

        Page<JobPosting> postings = jobPostingRepo.findAll(spec, pageable);

        if (postings.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy tin tuyển dụng phù hợp");
        }
        List<JobPostingDto> jobPostingDtos = postings.getContent().stream()
                .map(jobPostingMapper::toDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Tìm kiếm thành công!")
                .jobPostingDtoList(jobPostingDtos)
                .currentPage(postings.getNumber())
                .totalItems(postings.getTotalElements())
                .totalPages(postings.getTotalPages())
                .build();
    }

    @Override
    public Response fileterJobPostingsTheBetter(String location, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.desc("salaryMax"), Sort.Order.desc("createdAt")) // Ưu tiên lương cao, sau đó là mới nhất
        );

        Specification<JobPosting> spec = JobPostingSpecification.isActive();

        if (location != null && !location.isBlank()) {
            spec = spec.and(JobPostingSpecification.hasAddress(location));
        }

        Page<JobPosting> postings = jobPostingRepo.findAll(spec, pageable);

        if (postings.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy việc làm phù hợp tại " + location);
        }

        List<JobPostingDto> jobPostingDtos = postings.getContent().stream()
                .map(jobPostingMapper::toDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Lọc việc làm thành công!")
                .jobPostingDtoList(jobPostingDtos)
                .currentPage(postings.getNumber())
                .totalItems(postings.getTotalElements())
                .totalPages(postings.getTotalPages())
                .build();
    }

    @Override
    public Response filterJobPostings(String title, String companyName, String jobField, String location, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Specification<JobPosting> spec = Specification.allOf(
                JobPostingSpecification.isActive(),
                JobPostingSpecification.hasTitle(title),
                JobPostingSpecification.hasJobCategoryName(jobField),
                JobPostingSpecification.hasCompanyName(companyName),
                JobPostingSpecification.hasAddress(location)
        );
        Page<JobPosting> postings = jobPostingRepo.findAll(spec, pageable);

        if (postings.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy công việc phù hợp với bộ lọc!");
        }
        List<JobPostingCardDto> jobPostingDtos = postings.getContent().stream()
                .map(jobPostingMapper::toJobPostingCardDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Lọc công việc thành công!")
                .jobPostingCardDtoList(jobPostingDtos)
                .currentPage(postings.getNumber())
                .totalItems(postings.getTotalElements())
                .totalPages(postings.getTotalPages())
                .build();
    }

    @Override
    public Response getJobPostingsByCompany(Long employeeId, int page, int size) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà tuyển dụng"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<JobPosting> postings = jobPostingRepo.findByEmployee(employee, pageable);

        if (postings.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Nhà tuyển dụng chưa đăng tin tuyển dụng nào" );
        }
        List<JobPostingDto> jobPostingDtos = postings.getContent().stream()
                .map(this::checkAndUpdateExpiration)
                .map(jobPostingMapper::toDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Lấy danh sách tin tuyển dụng của công ty thành công")
                .jobPostingDtoList(jobPostingDtos)
                .currentPage(postings.getNumber())
                .totalItems(postings.getTotalElements())
                .totalPages(postings.getTotalPages())
                .build();
    }

    @Override
    public Response approveJobPosting(Long id) {
        JobPosting posting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng"));
        if (posting.getStatus() != JobPostingStatus.PENDING) {
            throw new BadRequestException("Chỉ có thể duyệt các tin tuyển dụng đang chờ");
        }
        posting.setStatus(JobPostingStatus.ACTIVE);
        JobPosting updated = jobPostingRepo.save(posting);
        JobPostingDto updatedDto = jobPostingMapper.toDto(updated);

        String employerEmail = updated.getEmployee().getUser().getEmail();
        String jobTitle = updated.getTitle();
        emailService.sendJobPostingStatusUpdateEmail(employerEmail, jobTitle, "Đã duyệt");
        return Response.builder()
                .status(200)
                .message("Duyệt tin tuyển dụng thành công")
                .jobPostingDto(updatedDto)
                .build();
    }

    @Override
    public Response lockJobPosting(Long id) {
        JobPosting posting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng"));
        if (posting.getStatus() == JobPostingStatus.LOCKED) {
            throw new BadRequestException("Tin tuyển dụng đã bị khóa trước đó");
        }
        posting.setStatus(JobPostingStatus.LOCKED);
        JobPosting updated = jobPostingRepo.save(posting);
        JobPostingDto updatedDto = jobPostingMapper.toDto(updated);

        String employerEmail = updated.getEmployee().getUser().getEmail();
        String jobTitle = updated.getTitle();
        emailService.sendJobPostingStatusUpdateEmail(employerEmail, jobTitle, "Đã khóa");
        return Response.builder()
                .status(200)
                .message("Khóa tin tuyển dụng thành công")
                .jobPostingDto(updatedDto)
                .build();
    }

    @Override
    public Response shareJobPosting(Long jobId) {
        JobPosting posting = jobPostingRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng"));
        if (posting.getStatus() != JobPostingStatus.ACTIVE) {
            throw new BadRequestException("Chỉ có thể chia sẻ các tin tuyển dụng đang hoạt động");
        }
        String token = jwtUtils.generateShareToken(jobId);
        String shareLink = "http://localhost:3000/public/job/" + token;
        return Response.builder()
                .status(200)
                .message("Tạo link chia sẻ tin tuyển dụng thành công")
                .shareLinkJob(shareLink)
                .build();
    }

    @Override
    public Response getJobPostingByShareToken(String token) {
        Long jobId = jwtUtils.parseJobIdFromToken(token);
        JobPosting posting = jobPostingRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng"));
        if (posting.getStatus() != JobPostingStatus.ACTIVE) {
            throw new BadRequestException("Chỉ có thể xem các tin tuyển dụng đang hoạt động");
        }
        JobPostingDto postingDto = jobPostingMapper.toDto(posting);
        return Response.builder()
                .status(200)
                .message("Lấy tin tuyển dụng thành công")
                .jobPostingDto(postingDto)
                .build();
    }

    @Override
    public Response getJobPostingsByAddress(String address, int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<JobPosting> postings;

        if (address != null && !address.isBlank()) {
            postings = jobPostingRepo.findByAddressContainingIgnoreCase(address, pageable);
        } else {
            postings = jobPostingRepo.findAll(pageable);
        }

        if (postings.isEmpty()) {
            throw new ResourceNotFoundException(
                    address != null && !address.isBlank()
                            ? "Không tìm thấy tin tuyển dụng tại địa chỉ: " + address
                            : "Không tìm thấy tin tuyển dụng nào"
            );
        }

        List<JobPostingDto> jobPostingDtos = postings.getContent().stream()
                .map(this::checkAndUpdateExpiration) // kiểm tra hết hạn
                .map(jobPostingMapper::toDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Lấy danh sách tin tuyển dụng theo địa chỉ thành công")
                .jobPostingDtoList(jobPostingDtos)
                .currentPage(postings.getNumber())
                .totalItems(postings.getTotalElements())
                .totalPages(postings.getTotalPages())
                .build();
    }

    @Override
    public Response getCandidatesForJobPosting(Long jobId, int page, int size, String sortDir, String status) {
        JobPosting posting = jobPostingRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Tin tuyển dụng không tồn tại với id: " + jobId));

        Sort sort = sortDir != null && sortDir.equalsIgnoreCase("asc")
                ? Sort.by("appliedAt").ascending()
                : Sort.by("appliedAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<JobApplication> applicationsPage;
        if (status != null && !status.isEmpty()) {
            JobApplicationStatus appStatus;
            try {
                appStatus = JobApplicationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResourceNotFoundException("Invalid status: " + status);
            }
            applicationsPage = jobApplicationRepo.findByJobPostingAndStatus(posting, appStatus, pageable);
        } else {
            applicationsPage = jobApplicationRepo.findByJobPosting(posting, pageable);
        }

        if (applicationsPage.isEmpty()) {
            throw new ResourceNotFoundException("Không có ứng viên nào ứng tuyển cho tin tuyển dụng này");
        }
        List<JobApplicationDto> applications = applicationsPage.getContent()
                .stream()
                .map(jobApplicationMapper::toDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Lấy danh sách ứng viên ứng tuyển thành công")
                .jobApplicationDtoList(applications)
                .currentPage(applicationsPage.getNumber())
                .totalItems(applicationsPage.getTotalElements())
                .totalPages(applicationsPage.getTotalPages())
                .build();
    }

    @Override
    public Response filterJobPostings(String title, String companyName, String address, Double minSalary, Double maxSalary, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<JobPosting> spec = Specification.allOf(
                JobPostingSpecification.isActive(),
                JobPostingSpecification.hasTitle(title),
                JobPostingSpecification.hasCompanyName(companyName),
                JobPostingSpecification.hasAddress(address),
                JobPostingSpecification.salaryBetween(minSalary, maxSalary)
        );

        Page<JobPosting> postings = jobPostingRepo.findAll(spec, pageable);

        if (postings.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy công việc phù hợp theo tiêu chí tìm kiếm");
        }

        List<JobPostingCardDto> jobPostingDtos = postings.getContent()
                .stream()
                .map(jobPostingMapper::toJobPostingCardDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Lọc công việc thành công!")
                .currentPage(postings.getNumber())
                .totalItems(postings.getTotalElements())
                .totalPages(postings.getTotalPages())
                .jobPostingCardDtoList(jobPostingDtos)
                .build();
    }
}
