package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;

public interface JobApplicationService {
    Response getAllJobApplications(int page, int size, String sortBy, String sortDir, String search);
    Response getJobApplicationById(Long jobApplicationId);
    Response getJobApplicationsByJobId(Long jobId, int page, int size, String sortBy, String sortDir);
    Response approveJobApplication(Long jobApplicationId);
    Response rejectJobApplication(Long jobApplicationId);
    Response filterJobApplicationsByStatus(String status, int page, int size, String sortBy, String sortDir);
}
