package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobPostingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String title;
    private String jobField;
    private String jobPosition;
    private String address;
    private Integer experienceYear;
    private Integer quantity;
    private Double salaryMin;
    private Double salaryMax;
    private Boolean negotiable;

    @Column(name = "endDate", columnDefinition = "DATETIME")
    private LocalDateTime endDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)  // Lưu dạng text: "PENDING", "LOCKED", ...
    @Column(nullable = false)
    private JobPostingStatus status = JobPostingStatus.PENDING;

    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

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
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
