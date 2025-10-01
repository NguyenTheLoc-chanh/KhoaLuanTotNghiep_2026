package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.FeedbackDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.FeedbackRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Feedback;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.FeedbackStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.FeedbackMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.FeedbackRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.UserRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.EmailService;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepo feedbackRepo;
    private final UserRepo userRepo;
    private final FeedbackMapper feedbackMapper;
    private final EmailService emailService;

    @Override
    public Response createFeedback(Long userId, FeedbackRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với id = " + userId));

        Feedback feedback = new Feedback();
        feedback.setTitle(request.getTitle());
        feedback.setDescription(request.getDescription());

        feedback.setUser(user);
        Feedback saved = feedbackRepo.save(feedback);

        return Response.builder()
                .status(201)
                .message("Tạo feedback thành công")
                .feed(feedbackMapper.toDto(saved))
                .build();
    }

    @Override
    public Response getAllFeedbacks(int page, int size) {
        Pageable pageable = buildPageable(page, size);
        Page<Feedback> feedbackPage = feedbackRepo.findAll(pageable);
        return buildPagedResponse(feedbackPage);
    }

    @Override
    public Response getFeedbackById(Long feedbackId) {
        Feedback feedback = feedbackRepo.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy feedback với id = " + feedbackId));

        return Response.builder()
                .status(200)
                .message("Lấy feedback thành công")
                .feed(feedbackMapper.toDto(feedback))
                .build();
    }

    @Override
    public Response updateFeedbackStatus(Long feedbackId, String status) {
        Feedback feedback = feedbackRepo.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy feedback với id = " + feedbackId));

        try {
            feedback.setStatus(FeedbackStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trạng thái feedback không hợp lệ: " + status);
        }

        Feedback updated = feedbackRepo.save(feedback);

        return Response.builder()
                .status(200)
                .message("Cập nhật trạng thái feedback thành công")
                .feed(feedbackMapper.toDto(updated))
                .build();
    }

    @Override
    public Response deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepo.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy feedback với id = " + feedbackId));

        feedbackRepo.delete(feedback);

        return Response.builder()
                .status(200)
                .message("Xóa feedback thành công")
                .build();
    }

    @Override
    public Response replyToFeedback(Long feedbackId, String replyContent) {
        Feedback feedback = feedbackRepo.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback không tồn tại"));
        emailService.sendFeedbackReplyEmail(feedback.getUser().getEmail(), replyContent);

        feedback.setStatus(FeedbackStatus.RESOLVED);

        feedbackRepo.save(feedback);

        return Response.builder()
                .status(200)
                .message("Phản hồi feedback thành công")
                .build();
    }

    @Override
    public Response getAllFeedbacksByUserId(Long userId, int page, int size) {
        Pageable pageable = buildPageable(page, size);
        Page<Feedback> feedbackPage = feedbackRepo.findByUser_UserId(userId, pageable);
        return buildPagedResponse(feedbackPage);
    }

    private Pageable buildPageable(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        return PageRequest.of(page, size, Sort.by("feedbackId").descending());
    }

    private Response buildPagedResponse(Page<Feedback> feedbackPage) {
        List<FeedbackDto> feedbackDtos = feedbackPage.getContent().stream()
                .map(feedbackMapper::toDto)
                .toList();

        if (feedbackDtos.isEmpty()) {
            throw new ResourceNotFoundException("Không có feedback nào");
        }

        return Response.builder()
                .status(200)
                .message("Lấy danh sách feedback thành công")
                .feedbackDtoList(feedbackDtos)
                .currentPage(feedbackPage.getNumber())
                .totalItems(feedbackPage.getTotalElements())
                .totalPages(feedbackPage.getTotalPages())
                .build();
    }
}
