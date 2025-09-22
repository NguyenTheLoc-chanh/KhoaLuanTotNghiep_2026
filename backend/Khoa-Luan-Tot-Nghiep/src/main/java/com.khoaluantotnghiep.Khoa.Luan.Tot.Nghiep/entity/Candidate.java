package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tblCandidate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long candidateId;

    private String avatar;
    private Integer experienceYear;
    private String fCv;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<JobApplication> jobApplications;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<CandidateFavoriteJob> favoriteJobs;
}
