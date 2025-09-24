package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.FeedbackRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;

public interface FeedbackService {
    Response createFeedback(Long userId, FeedbackRequest request);
    Response getAllFeedbacks(int page, int size);
    Response getFeedbackById(Long feedbackId);
    Response updateFeedbackStatus(Long feedbackId, String status);
    Response deleteFeedback(Long feedbackId);
}
