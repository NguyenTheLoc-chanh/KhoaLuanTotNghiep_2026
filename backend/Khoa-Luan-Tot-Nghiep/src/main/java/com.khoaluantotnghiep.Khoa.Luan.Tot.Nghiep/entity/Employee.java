package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Entity
@Table(name = "tblEmployee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    private String avatar;
    private String address;
    private String scale;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String companyName;
    private String companyLogo;
    private String website;
    private String businessLicense;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<JobPosting> jobPostings;
}
