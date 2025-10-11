package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobPostingStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tblJobPosting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "deadline", columnDefinition = "DATE")
    private LocalDate deadline;

    @Column(name = "job_benefit", columnDefinition = "TEXT")
    private String jobBenefit;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "job_exp")
    private String job_exp;

    @Column(name = "job_requirement", columnDefinition = "TEXT")
    private String jobRequirement;

    private String salary;

    private String title;

    private String type;

    @Column(name = "working_times", columnDefinition = "TEXT")
    private String workingTimes;

    private Integer quantity;

    @Enumerated(EnumType.STRING)  // Lưu dạng text: "PENDING", "LOCKED", ...
    @Column(nullable = false)
    private JobPostingStatus status = JobPostingStatus.PENDING;

    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "jobCategoryId")
    private JobCategory jobCategory;


    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL)
    private List<JobApplication> jobApplications;

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL)
    private List<CandidateFavoriteJob> favoriteJobs;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
