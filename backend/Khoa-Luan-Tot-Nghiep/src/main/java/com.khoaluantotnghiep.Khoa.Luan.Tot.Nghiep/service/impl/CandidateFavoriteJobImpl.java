package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.CandidateFavoriteJobDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Candidate;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.CandidateFavoriteJob;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.BadRequestException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.CandidateFavoriteJobMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.CandidateFavoriteJobRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.CandidateRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.JobPostingRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.CandidateFavoriteJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateFavoriteJobImpl implements CandidateFavoriteJobService {

    private final CandidateRepo candidateRepo;
    private  final JobPostingRepo jobPostingRepo;
    private final CandidateFavoriteJobRepo candidateFavoriteJobRepo;
    private final CandidateFavoriteJobMapper candidateFavoriteJobMapper;

    @Override
    public Response addFavoriteJob(Long candidateId, Long jobId) {
        Candidate candidate = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id " + candidateId));
        JobPosting jobPosting = jobPostingRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id " + jobId));
        // Logic to add favorite job
        boolean exists = candidateFavoriteJobRepo.existsByCandidate_CandidateIdAndJobPosting_JobId(candidateId, jobId);
        if (exists) {
            throw new BadRequestException("Job is already in favorites");
        }
        // Save favorite job logic here
        CandidateFavoriteJob favorite = new CandidateFavoriteJob();
        favorite.setCandidate(candidate);
        favorite.setJobPosting(jobPosting);

        candidateFavoriteJobRepo.save(favorite);
        CandidateFavoriteJobDto candidateFavoriteJobDto = candidateFavoriteJobMapper.toDto(favorite);

        return Response.builder()
                .status(201)
                .message("Job added to favorites successfully")
                .candidateFavoriteJobDto(candidateFavoriteJobDto)
                .build();
    }

    @Override
    public Response removeFavoriteJob(Long candidateId, Long jobId) {
        CandidateFavoriteJob favorite = candidateFavoriteJobRepo
                .findByCandidate_CandidateIdAndJobPosting_JobId(candidateId, jobId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Favorite job not found for candidate " + candidateId + " and job " + jobId
                ));

        candidateFavoriteJobRepo.delete(favorite);

        return Response.builder()
                .status(200)
                .message("Job removed from favorites successfully")
                .build();
    }

    @Override
    public Response getFavoriteJobs(Long candidateId) {
        // Check ứng viên có tồn tại
        candidateRepo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id " + candidateId));

        List<CandidateFavoriteJob> favorites = candidateFavoriteJobRepo.findByCandidate_CandidateId(candidateId);

        List<CandidateFavoriteJobDto> favoriteDtos = favorites.stream()
                .map(candidateFavoriteJobMapper::toDto)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .message("Fetched favorite jobs successfully")
                .candidateFavoriteJobDtoList(favoriteDtos)
                .build();
    }
}
