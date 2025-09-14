package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.FeedbackStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)  // Lưu "PENDING" hoặc "RESOLVED"
    @Column(nullable = false)
    private FeedbackStatus status  = FeedbackStatus.PENDING;

    @Column(name = "created_at", updatable = false,columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Column(name = "updated_at",columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

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
