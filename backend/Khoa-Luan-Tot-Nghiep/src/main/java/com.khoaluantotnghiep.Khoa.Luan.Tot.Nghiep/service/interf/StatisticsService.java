package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;

public interface StatisticsService {
    Response candidateStatistics();
    Response employeeStatistics();
    Response feedbackStatistics();
    Response jobPostingStatistics();
    Response sampleCVStatistics();
}
