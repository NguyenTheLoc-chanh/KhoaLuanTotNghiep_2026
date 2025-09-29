package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Candidate;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Feedback;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.SampleCV;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.FeedbackStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobPostingStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final CandidateRepo candidateRepo;
    private final EmployeeRepo employeeRepo;
    private final FeedbackRepo feedbackRepo;
    private final JobPostingRepo jobPostingRepo;
    private final SampleCVRepo sampleCVRepo;

    @Override
    public Response candidateStatistics() {
        List<Candidate> candidates = candidateRepo.findAll();
        int totalCandidates = candidates.size();
        return Response.builder()
                .status(200)
                .message("Candidate statistics retrieved successfully")
                .statistics(Map.of(
                        "totalCandidates", totalCandidates
                ))
                .build();
    }

    @Override
    public Response employeeStatistics() {
        Long totalEmployees = employeeRepo.count();
        return Response.builder()
                .status(200)
                .message("Employee statistics retrieved successfully")
                .statistics(Map.of(
                        "totalEmployees", totalEmployees
                ))
                .build();
    }

    @Override
    public Response feedbackStatistics() {
        List<Feedback> feedbacks = feedbackRepo.findAll();
        if (feedbacks.isEmpty()) {
            throw new ResourceNotFoundException("Không có feedback nào");
        }
        long totalFeedbacks = feedbacks.size();
        long pendingCount = feedbacks.stream()
                .filter(f -> f.getStatus() == FeedbackStatus.PENDING)
                .count();
        long resolvedCount = feedbacks.stream()
                .filter(f -> f.getStatus() == FeedbackStatus.RESOLVED)
                .count();
        return Response.builder()
                .status(200)
                .message("Thống kê feedback thành công")
                .statistics(Map.of(
                        "total", totalFeedbacks,
                        "pending", pendingCount,
                        "approved", resolvedCount
                ))
                .build();
    }

    @Override
    public Response jobPostingStatistics() {
        List<JobPosting> postings = jobPostingRepo.findAll();
        if (postings.isEmpty()) {
            throw new ResourceNotFoundException("Không có bài đăng việc làm nào");
        }
        long totalPostings = postings.size();
        long activeCount = postings.stream()
                .filter(p -> p.getStatus() == JobPostingStatus.ACTIVE)
                .count();
        long pendingCount = postings.stream()
                .filter(p -> p.getStatus() == JobPostingStatus.PENDING)
                .count();
        long lockedCount = postings.stream()
                .filter(p -> p.getStatus() == JobPostingStatus.LOCKED)
                .count();
        long expiredCount = postings.stream()
                .filter(p -> p.getStatus() == JobPostingStatus.EXPIRED)
                .count();
        return Response.builder()
                .status(200)
                .message("Thống kê bài đăng việc làm thành công!")
                .statistics(Map.of(
                        "totalPostings", totalPostings,
                        "activeCount", activeCount,
                        "pendingCount", pendingCount,
                        "lockedCount", lockedCount,
                        "expiredCount", expiredCount
                ))
                .build();
    }

    @Override
    public Response sampleCVStatistics() {
        List<SampleCV> sampleCVs = sampleCVRepo.findAll();
        if (sampleCVs.isEmpty()) {
            throw new ResourceNotFoundException("Không có mẫu CV nào");
        }
        int totalSampleCVs = sampleCVs.size();
        return Response.builder()
                .status(200)
                .message("Thống kê mẫu CV thành công")
                .statistics(Map.of(
                        "totalSampleCVs", totalSampleCVs
                ))
                .build();
    }
}
