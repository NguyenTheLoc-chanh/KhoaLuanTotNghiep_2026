package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;

import java.util.List;

public interface CandidateFavoriteJobService {
    // Thêm công việc vào danh sách yêu thích của ứng viên
    Response addFavoriteJob(Long candidateId, Long jobId);

    // Xóa công việc khỏi danh sách yêu thích của ứng viên
    Response removeFavoriteJob(Long candidateId, Long jobId);

    // Lấy danh sách công việc yêu thích của ứng viên
    Response getFavoriteJobs(Long candidateId);
}
