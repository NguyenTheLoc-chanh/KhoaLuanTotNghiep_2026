package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobPostingDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;

public interface JobPostingService {
    Response createJobPosting(JobPostingDto jobPostingDto);
    Response getAllJobPostings(int page, int size);
    Response getJobPostingById(Long id);
    Response updateJobPosting(Long id, JobPostingDto jobPostingDto);
    Response deleteJobPosting(Long id);
    Response searchJobPostings(String keyword, int page, int size);
    Response filterJobPostings(String location, String jobType, int page, int size);
    Response getJobPostingsByCompany(Long employeeId, int page, int size);
    Response approveJobPosting(Long id);
    Response lockJobPosting(Long id);
    Response shareJobPosting(Long jobId);
    Response getJobPostingByShareToken(String token);
}
