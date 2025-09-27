package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.CandidateDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobApplicationDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobApplicationRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Candidate;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobApplication;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.CandidateMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.JobApplicationMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.CandidateRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.JobApplicationRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.JobPostingRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.UserRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.CloudinaryService;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepo candidateRepo;
    private final UserRepo userRepo;
    private final CandidateMapper candidateMapper;
    private final CloudinaryService cloudinaryService;
    private final JobApplicationRepo jobApplicationRepo;
    private final JobPostingRepo jobPostingRepo;
    private final JobApplicationMapper jobApplicationMapper;

    @Override
    public Response createCandidate(CandidateDto candidateDto) {
        Candidate candidate = candidateMapper.toEntity(candidateDto);

        // Gắn user nếu có userId
        attachUserToCandidate(candidate, candidateDto.getUserId());

        Candidate savedCandidate = candidateRepo.save(candidate);
        CandidateDto savedDto = candidateMapper.toDto(savedCandidate);

        return Response.builder()
                .status(201)
                .message("Candidate created successfully")
                .candidateDto(savedDto)
                .build();
    }

    @Override
    public Response getCandidateByUserId(Long userId) {
        Candidate candidate = candidateRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with userId: " + userId));
        CandidateDto candidateDto = candidateMapper.toDto(candidate);
        return Response.builder()
                .status(200)
                .message("Candidate retrieved successfully")
                .candidateDto(candidateDto)
                .build();
    }

    @Override
    public Response updateCandidateInfo(CandidateDto candidateDto) {
        Candidate candidate = candidateRepo.findByUser_UserId(candidateDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + candidateDto.getCandidateId()));

        candidate.setExperienceYear(candidateDto.getExperienceYear());
        candidate.setFCv(candidateDto.getFCv());

        // update user nếu có
        attachUserToCandidate(candidate, candidateDto.getUserId());

        Candidate updatedCandidate = candidateRepo.save(candidate);
        CandidateDto updatedDto = candidateMapper.toDto(updatedCandidate);

        return Response.builder()
                .status(200)
                .message("Candidate info updated successfully")
                .candidateDto(updatedDto)
                .build();
    }

    @Override
    public Response updateCandidateAvatar(Long userId, MultipartFile avatarFile) {
        Candidate candidate = candidateRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with userId: " + userId));

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarUrl = cloudinaryService.uploadImage(avatarFile);
            candidate.setAvatar(avatarUrl);
        }

        Candidate updatedCandidate = candidateRepo.save(candidate);
        CandidateDto updatedDto = candidateMapper.toDto(updatedCandidate);

        return Response.builder()
                .status(200)
                .message("Candidate avatar updated successfully")
                .candidateDto(updatedDto)
                .build();
    }

    @Override
    public Response updateCandidateFCv(Long userId, MultipartFile fCvFile) {
        Candidate candidate = candidateRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with userId: " + userId));
        if (fCvFile != null && !fCvFile.isEmpty()) {
            String fCvUrl = cloudinaryService.uploadPdf(fCvFile);
            candidate.setFCv(fCvUrl);
        }
        Candidate updatedCandidate = candidateRepo.save(candidate);
        CandidateDto updatedDto = candidateMapper.toDto(updatedCandidate);
        return Response.builder()
                .status(200)
                .message("Candidate CV updated successfully")
                .candidateDto(updatedDto)
                .build();
    }

    @Override
    public Response deleteCandidate(Long userId) {
        Candidate candidate = candidateRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with userId: " + userId));
        if (candidate == null) {
            return Response.builder()
                    .status(404)
                    .message("Candidate not found")
                    .build();
        }
        candidateRepo.delete(candidate);
        return Response.builder()
                .status(200)
                .message("Candidate deleted successfully")
                .build();
    }

    @Override
    public Response getAllCandidates(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Candidate> candidatePage;
        if (search != null && !search.trim().isEmpty()) {
            candidatePage = candidateRepo.findByUser_FullNameContainingIgnoreCase(search.trim(), pageable);
        } else {
            candidatePage = candidateRepo.findAll(pageable);
        }
        List<CandidateDto> candidateDtos = candidatePage.getContent()
                .stream()
                .map(candidateMapper::toDto)
                .toList();
        return Response.builder()
                .status(200)
                .message("Candidates retrieved successfully")
                .candidateDtoList(candidateDtos)
                .currentPage(candidatePage.getNumber())
                .totalItems(candidatePage.getTotalElements())
                .totalPages(candidatePage.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public Response submitApplication(Long candidateId, JobApplicationRequest applyRequest) {
        Candidate candidate = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + candidateId));
        if (candidate == null) {
            return Response.builder()
                    .status(404)
                    .message("Candidate not found")
                    .build();
        }
        JobPosting jobPosting = jobPostingRepo.findById(applyRequest.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id: " + applyRequest.getJobId()));
        if (jobPosting == null) {
            return Response.builder()
                    .status(404)
                    .message("Job posting not found")
                    .build();
        }
        jobApplicationRepo.findByCandidate_CandidateIdAndJobPosting_JobId(candidateId, applyRequest.getJobId())
                .ifPresent(a -> {
                    throw new IllegalStateException("You have already applied for this job");
                });
        if (applyRequest.getFCvFile() == null || applyRequest.getFCvFile().isEmpty()) {
            throw new IllegalArgumentException("CV file is required");
        }
        if (applyRequest.getFullName() == null || applyRequest.getEmail() == null || applyRequest.getPhone() == null) {
            throw new IllegalArgumentException("Missing required information");
        }
        JobApplication application = new JobApplication();
        application.setCandidate(candidate);
        application.setJobPosting(jobPosting);
        application.setFullName(applyRequest.getFullName());
        application.setEmail(applyRequest.getEmail());
        application.setPhone(applyRequest.getPhone());

        String fCvUrl = cloudinaryService.uploadPdf(applyRequest.getFCvFile());
        application.setFCv(fCvUrl);

        JobApplication savedApplication = jobApplicationRepo.save(application);
        JobApplicationDto applicationDto = jobApplicationMapper.toDto(savedApplication);
        return Response.builder()
                .status(201)
                .message("Job application submitted successfully")
                .jobApplicationDto(applicationDto)
                .build();
    }

    private void attachUserToCandidate(Candidate candidate, Long userId) {
        if (userId != null) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            candidate.setUser(user);
        }
    }
}
