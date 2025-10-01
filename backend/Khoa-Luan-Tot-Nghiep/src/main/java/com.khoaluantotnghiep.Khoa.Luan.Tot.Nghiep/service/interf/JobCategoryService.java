package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.JobCategoryRequest;

public interface JobCategoryService {
    Response createJobCategory(Long userId, JobCategoryRequest request);
    Response getAllJobCategories(int page, int size);
    Response getJobCategoryById(Long jobCategoryId);
    Response updateJobCategory(Long jobCategoryId, JobCategoryRequest request);
    Response deleteJobCategory(Long jobCategoryId);
    Response getJobCategoryStats();
    Response getAllJobPostingsByCategoryId(Long categoryId, int page, int size);
}
