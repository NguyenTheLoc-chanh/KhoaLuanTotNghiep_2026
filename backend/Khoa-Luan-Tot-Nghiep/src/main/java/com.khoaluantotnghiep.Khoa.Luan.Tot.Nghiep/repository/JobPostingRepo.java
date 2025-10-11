package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Employee;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobPostingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface JobPostingRepo extends JpaRepository<JobPosting,Long>, JpaSpecificationExecutor<JobPosting> {
    Page<JobPosting> findByEmployeeAndStatus(Employee employee, JobPostingStatus status, Pageable pageable);

    Page<JobPosting> findByJobCategory_JobCategoryIdAndStatus(Long jobCategoryId, JobPostingStatus status, Pageable pageable);

    Page<JobPosting> findByAddressContainingIgnoreCase(String address, Pageable pageable);

    // Tìm tất cả bài đăng theo nhân viên đã tạo
    Page<JobPosting> findByEmployee(Employee employee, Pageable pageable);

    @Modifying
    @Query("UPDATE JobPosting j SET j.status = 'EXPIRED' WHERE j.deadline < :now AND j.status = 'ACTIVE'")
    void updateExpiredJobs(@Param("now") LocalDate now);

    @Query("SELECT j FROM JobPosting j " +
            "JOIN j.employee e " +
            "WHERE j.status = com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobPostingStatus.ACTIVE " +
            "AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "     OR LOWER(j.address) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "     OR LOWER(e.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) )")
    Page<JobPosting> searchActiveJobPostings(@Param("keyword") String keyword, Pageable pageable);

}
