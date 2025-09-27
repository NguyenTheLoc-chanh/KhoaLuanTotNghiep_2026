package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Employee;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobPostingStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.BadRequestException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.JobPostingMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.EmployeeRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.JobPostingRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.JwtUtils;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.EmailService;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepo jobPostingRepo;
    private final EmployeeRepo employeeRepo;
    private final JobPostingMapper jobPostingMapper;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    private void validateJobPostingDto(JobPostingDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new BadRequestException("Job title cannot be empty");
        }
        if (dto.getEmployee().getEmployeeId() == null) {
            throw new BadRequestException("EmployeeId is required");
        }
    }

    private JobPosting buildJobPosting(JobPostingDto dto, Employee employee) {
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
    public Response createJobPosting(JobPostingDto jobPostingDto) {
        validateJobPostingDto(jobPostingDto);
        Employee employee = employeeRepo.findById(jobPostingDto.getEmployee().getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + jobPostingDto.getEmployee().getEmployeeId()));

        JobPosting jobPosting = buildJobPosting(jobPostingDto, employee);
        JobPosting saved = jobPostingRepo.save(jobPosting);
        JobPostingDto savedDto = jobPostingMapper.toDto(saved);

        return Response.builder()
                .status(201)
                .message("Job posting created successfully")
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
            throw new ResourceNotFoundException("No job postings found");
        }

        return Response.builder()
                .status(200)
                .message("Job postings retrieved successfully")
                .jobPostingDtoList(jobPostingDtos)
                .currentPage(postings.getNumber())
                .totalItems(postings.getTotalElements())
                .totalPages(postings.getTotalPages())
                .build();
    }

    @Override
    public Response getJobPostingById(Long id) {
        JobPosting posting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id " + id));

        posting = checkAndUpdateExpiration(posting);

        JobPostingDto postingDto = jobPostingMapper.toDto(posting);

        return Response.builder()
                .status(200)
                .message("Job posting retrieved successfully")
                .jobPostingDto(postingDto)
                .build();
    }

    @Override
    public Response updateJobPosting(Long id, JobPostingDto jobPostingDto) {
        JobPosting posting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id " + id));

        if (jobPostingDto.getTitle() != null && !jobPostingDto.getTitle().isBlank()) posting.setTitle(jobPostingDto.getTitle());
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
                .message("Job posting updated successfully")
                .jobPostingDto(updatedDto)
                .build();
    }

    @Override
    public Response deleteJobPosting(Long id) {
        JobPosting posting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id " + id));

        jobPostingRepo.delete(posting);

        return Response.builder()
                .status(200)
                .message("Job posting deleted successfully")
                .build();
    }

    @Override
    public Response searchJobPostings(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<JobPosting> postings = jobPostingRepo.findByTitleContainingIgnoreCase(keyword, pageable);

        if (postings.getContent().isEmpty()) {
            throw new ResourceNotFoundException("No job postings found matching the keyword: " + keyword);
        }
        List<JobPostingDto> jobPostingDtos = postings.getContent().stream()
                .map(jobPostingMapper::toDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Search completed successfully")
                .jobPostingDtoList(jobPostingDtos)
                .currentPage(postings.getNumber())
                .totalItems(postings.getTotalElements())
                .totalPages(postings.getTotalPages())
                .build();
    }

    @Override
    public Response filterJobPostings(String location, String jobType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<JobPosting> postings = jobPostingRepo.findByAddressContainingIgnoreCaseAndJobFieldContainingIgnoreCase(
                location != null ? location : "",
                jobType != null ? jobType : "",
                pageable
        );
        if (postings.getContent().isEmpty()) {
            throw new ResourceNotFoundException("No job postings found for the given filters");
        }
        List<JobPostingDto> jobPostingDtos = postings.getContent().stream()
                .map(jobPostingMapper::toDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Filter completed successfully")
                .jobPostingDtoList(jobPostingDtos)
                .currentPage(postings.getNumber())
                .totalItems(postings.getTotalElements())
                .totalPages(postings.getTotalPages())
                .build();
    }

    @Override
    public Response getJobPostingsByCompany(Long employeeId, int page, int size) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + employeeId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<JobPosting> postings = jobPostingRepo.findByEmployee(employee, pageable);

        if (postings.getContent().isEmpty()) {
            throw new ResourceNotFoundException("No job postings found for the company with id " + employeeId);
        }
        List<JobPostingDto> jobPostingDtos = postings.getContent().stream()
                .map(this::checkAndUpdateExpiration)
                .map(jobPostingMapper::toDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Job postings by company retrieved successfully")
                .jobPostingDtoList(jobPostingDtos)
                .currentPage(postings.getNumber())
                .totalItems(postings.getTotalElements())
                .totalPages(postings.getTotalPages())
                .build();
    }

    @Override
    public Response approveJobPosting(Long id) {
        JobPosting posting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id " + id));
        if (posting.getStatus() != JobPostingStatus.PENDING) {
            throw new BadRequestException("Only pending job postings can be approved");
        }
        posting.setStatus(JobPostingStatus.ACTIVE);
        JobPosting updated = jobPostingRepo.save(posting);
        JobPostingDto updatedDto = jobPostingMapper.toDto(updated);

        String employerEmail = updated.getEmployee().getUser().getEmail();
        String jobTitle = updated.getTitle();
        emailService.sendJobPostingStatusUpdateEmail(employerEmail, jobTitle, "Đã duyệt");
        return Response.builder()
                .status(200)
                .message("Job posting approved successfully")
                .jobPostingDto(updatedDto)
                .build();
    }

    @Override
    public Response lockJobPosting(Long id) {
        JobPosting posting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id " + id));
        if (posting.getStatus() == JobPostingStatus.LOCKED) {
            throw new BadRequestException("Job posting is already locked");
        }
        posting.setStatus(JobPostingStatus.LOCKED);
        JobPosting updated = jobPostingRepo.save(posting);
        JobPostingDto updatedDto = jobPostingMapper.toDto(updated);

        String employerEmail = updated.getEmployee().getUser().getEmail();
        String jobTitle = updated.getTitle();
        emailService.sendJobPostingStatusUpdateEmail(employerEmail, jobTitle, "Đã khóa");
        return Response.builder()
                .status(200)
                .message("Job posting locked successfully")
                .jobPostingDto(updatedDto)
                .build();
    }

    @Override
    public Response shareJobPosting(Long jobId) {
        JobPosting posting = jobPostingRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id " + jobId));
        if (posting.getStatus() != JobPostingStatus.ACTIVE) {
            throw new BadRequestException("Only active job postings can be shared");
        }
        String token = jwtUtils.generateShareToken(jobId);
        String shareLink = "http://localhost:3000/public/job/" + token;
        return Response.builder()
                .status(200)
                .message("Job posting share token generated successfully")
                .shareLinkJob(shareLink)
                .build();
    }

    @Override
    public Response getJobPostingByShareToken(String token) {
        Long jobId = jwtUtils.parseJobIdFromToken(token);
        JobPosting posting = jobPostingRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id " + jobId));
        if (posting.getStatus() != JobPostingStatus.ACTIVE) {
            throw new BadRequestException("The job posting is not active");
        }
        JobPostingDto postingDto = jobPostingMapper.toDto(posting);
        return Response.builder()
                .status(200)
                .message("Job posting retrieved successfully from share token")
                .jobPostingDto(postingDto)
                .build();
    }
}
