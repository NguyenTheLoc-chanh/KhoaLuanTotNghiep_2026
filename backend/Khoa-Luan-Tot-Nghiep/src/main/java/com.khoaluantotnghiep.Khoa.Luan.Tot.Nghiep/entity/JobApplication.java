package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobApplicationStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobPostingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tblJob_Application")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobApplicationId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String fCv;

    @Column(name = "applied_at", updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime appliedAt;

    @Enumerated(EnumType.STRING)  // Lưu dạng text: "PENDING", "LOCKED", ...
    @Column(nullable = false)
    private JobApplicationStatus status = JobApplicationStatus.SUBMITTED;

    @ManyToOne
    @JoinColumn(name = "jobId")
    private JobPosting jobPosting;

    @ManyToOne
    @JoinColumn(name = "candidateId")
    private Candidate candidate;

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
    }
}
