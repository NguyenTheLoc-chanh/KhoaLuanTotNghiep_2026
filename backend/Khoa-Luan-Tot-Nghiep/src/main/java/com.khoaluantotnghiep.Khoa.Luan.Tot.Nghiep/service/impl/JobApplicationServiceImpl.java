package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobApplicationDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobApplication;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobApplicationStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.BadRequestException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.JobApplicationMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.JobApplicationRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepo jobApplicationRepo;
    private final JobApplicationMapper jobApplicationMapper;

    @Override
    public Response getAllJobApplications(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<JobApplication> jobApps = jobApplicationRepo.findAll(pageable);
        List<JobApplicationDto> dtoList = jobApps.stream()
                .map(jobApplicationMapper::toDto)
                .filter(dto -> search == null || dto.getFullName().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .message("Lấy danh sách ứng tuyển thành công!")
                .jobApplicationDtoList(dtoList)
                .build();
    }

    @Override
    public Response getJobApplicationById(Long jobApplicationId) {
        JobApplication jobApp = jobApplicationRepo.findById(jobApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ứng tuyển ID = " + jobApplicationId));

        return Response.builder()
                .status(200)
                .message("Lấy ứng tuyển thành công")
                .jobApplicationDto(jobApplicationMapper.toDto(jobApp))
                .build();
    }

    @Override
    public Response getJobApplicationsByJobId(Long jobId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<JobApplication> jobApplications = jobApplicationRepo.findByJobPosting_JobId(jobId, pageable);
        if (jobApplications.isEmpty()) {
            throw new ResourceNotFoundException("No job applications found for jobId = " + jobId);
        }

        // Map sang DTO (ví dụ JobApplicationDto)
        List<JobApplicationDto> dtoList = jobApplications.getContent()
                .stream()
                .map(jobApplicationMapper::toDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Get job applications successfully")
                .jobApplicationDtoList(dtoList)
                .currentPage(jobApplications.getNumber())
                .totalItems(jobApplications.getTotalElements())
                .totalPages(jobApplications.getTotalPages())
                .build();
    }

    @Override
    public Response approveJobApplication(Long jobApplicationId) {
        JobApplication jobApp = jobApplicationRepo.findById(jobApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ứng tuyển ID = " + jobApplicationId));

        jobApp.setStatus(JobApplicationStatus.VIEWED);
        jobApplicationRepo.save(jobApp);

        return Response.builder()
                .status(200)
                .message("Ứng tuyển đã được duyệt")
                .jobApplicationDto(jobApplicationMapper.toDto(jobApp))
                .build();
    }

    @Override
    public Response rejectJobApplication(Long jobApplicationId) {
        JobApplication jobApp = jobApplicationRepo.findById(jobApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ứng tuyển ID = " + jobApplicationId));

        jobApp.setStatus(JobApplicationStatus.REJECTED);
        jobApplicationRepo.save(jobApp);

        return Response.builder()
                .status(200)
                .message("Ứng tuyển đã bị từ chối")
                .jobApplicationDto(jobApplicationMapper.toDto(jobApp))
                .build();
    }

    @Override
    public Response filterJobApplicationsByStatus(String status, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<JobApplication> jobApps = jobApplicationRepo.findAll(pageable);

        JobApplicationStatus enumStatus;
        try {
            enumStatus = JobApplicationStatus.valueOf(status.toUpperCase()); // convert String -> Enum
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trạng thái không hợp lệ: " + status);
        }

        List<JobApplicationDto> dtoList = jobApps.stream()
                .filter(job -> job.getStatus() == enumStatus)
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .message("Lọc ứng tuyển theo trạng thái thành công")
                .jobApplicationDtoList(dtoList)
                .build();
    }
}
