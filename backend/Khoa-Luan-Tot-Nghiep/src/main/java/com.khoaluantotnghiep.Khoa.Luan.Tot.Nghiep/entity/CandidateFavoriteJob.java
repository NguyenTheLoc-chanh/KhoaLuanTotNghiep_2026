package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tblCandidateFavoriteJobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateFavoriteJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoriteJobId;

    @ManyToOne
    @JoinColumn(name = "job_id", referencedColumnName = "jobId", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne
    @JoinColumn(name = "candidate_id", referencedColumnName = "candidateId", nullable = false)
    private Candidate candidate;

    @Column(name = "saveDate", columnDefinition = "DATETIME")
    private LocalDateTime saveDate;
}
