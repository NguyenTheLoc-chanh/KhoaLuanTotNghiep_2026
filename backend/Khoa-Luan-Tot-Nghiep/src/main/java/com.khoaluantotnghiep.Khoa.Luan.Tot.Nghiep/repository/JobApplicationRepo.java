package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobApplication;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobApplicationRepo extends JpaRepository<JobApplication, Long> {
    Optional<JobApplication> findByCandidate_CandidateIdAndJobPosting_JobId(Long candidateId, Long jobId);
    Page<JobApplication> findByJobPosting_JobId(Long jobId, Pageable pageable);
    Page<JobApplication> findByCandidate_CandidateId(Long candidateId, Pageable pageable);
    Page<JobApplication> findByJobPostingAndStatus(JobPosting jobPosting,
                                                   JobApplicationStatus status,
                                                   Pageable pageable);
    Page<JobApplication> findByJobPosting(JobPosting jobPosting, Pageable pageable);

}
