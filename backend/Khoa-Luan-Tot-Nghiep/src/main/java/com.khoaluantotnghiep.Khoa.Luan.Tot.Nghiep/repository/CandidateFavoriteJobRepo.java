package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.CandidateFavoriteJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateFavoriteJobRepo extends JpaRepository<CandidateFavoriteJob, Long> {
    List<CandidateFavoriteJob> findByCandidate_CandidateId(Long candidateId);

    Optional<CandidateFavoriteJob> findByCandidate_CandidateIdAndJobPosting_JobId(Long candidateId, Long jobId);

    boolean existsByCandidate_CandidateIdAndJobPosting_JobId(Long candidateId, Long jobId);
}
