package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobCategoryDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.JobCategoryRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobCategory;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.JobCategoryMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.JobCategoryRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.UserRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.JobCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobCategoryServiceImpl implements JobCategoryService {

    private final JobCategoryRepo jobCategoryRepo;
    private final UserRepo userRepo;
    private final JobCategoryMapper jobCategoryMapper;

    @Override
    public Response createJobCategory(Long userId, JobCategoryRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với id = " + userId));
        JobCategory jobCategory = new JobCategory();
        jobCategory.setName(request.getName());
        jobCategory.setDescription(request.getDescription());

        jobCategory.setUser(user);
        JobCategory saved = jobCategoryRepo.save(jobCategory);

        return Response.builder()
                .status(201)
                .message("Tạo danh mục công việc thành công")
                .jobCategoryDto(jobCategoryMapper.toDto(saved))
                .build();
    }

    @Override
    public Response getAllJobCategories(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("jobCategoryId").descending());
        Page<JobCategory> jobCategoryPage = jobCategoryRepo.findAll(pageable);

        List<JobCategoryDto> jobCategoryDtos = jobCategoryPage.getContent().stream()
                .map(jobCategoryMapper::toDto)
                .toList();
        if (jobCategoryDtos.isEmpty()) {
            throw new ResourceNotFoundException("Không có danh mục công việc nào");
        }

        return Response.builder()
                .status(200)
                .message("Lấy danh sách danh mục công việc thành công")
                .jobCategoryDtoList(jobCategoryDtos)
                .currentPage(jobCategoryPage.getNumber())
                .totalItems(jobCategoryPage.getTotalElements())
                .totalPages(jobCategoryPage.getTotalPages())
                .build();
    }

    @Override
    public Response getJobCategoryById(Long jobCategoryId) {
        JobCategory jobCategory = jobCategoryRepo.findById(jobCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục công việc với id = " + jobCategoryId));
        JobCategoryDto jobCategoryDto = jobCategoryMapper.toDto(jobCategory);

        return Response.builder()
                .status(200)
                .message("Lấy danh mục công việc thành công")
                .jobCategoryDto(jobCategoryDto)
                .build();
    }

    @Override
    public Response updateJobCategory(Long jobCategoryId, JobCategoryRequest request) {
        JobCategory jobCategory = jobCategoryRepo.findById(jobCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục công việc với id = " + jobCategoryId));
        jobCategory.setName(request.getName());
        jobCategory.setDescription(request.getDescription());
        JobCategory updated = jobCategoryRepo.save(jobCategory);

        return Response.builder()
                .status(200)
                .message("Cập nhật danh mục công việc thành công")
                .jobCategoryDto(jobCategoryMapper.toDto(updated))
                .build();
    }

    @Override
    public Response deleteJobCategory(Long jobCategoryId) {
        JobCategory jobCategory = jobCategoryRepo.findById(jobCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục công việc với id = " + jobCategoryId));

        jobCategoryRepo.delete(jobCategory);

        return Response.builder()
                .status(200)
                .message("Xóa danh mục công việc thành công")
                .build();
    }
}
