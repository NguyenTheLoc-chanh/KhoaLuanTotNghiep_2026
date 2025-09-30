package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tblJobCategory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobCategoryId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    // Người quản trị tạo/sửa
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // Một ngành nghề có nhiều tin tuyển dụng
    @OneToMany(mappedBy = "jobCategory", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<JobPosting> jobPostings;

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
