package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Employee;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JobPostingRepo extends JpaRepository<JobPosting,Long> {
    // Tìm theo tiêu đề (không phân biệt hoa thường, chứa keyword)
    Page<JobPosting> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Tìm theo địa chỉ và lĩnh vực công việc (cả 2 đều chứa, không phân biệt hoa thường)
    Page<JobPosting> findByAddressContainingIgnoreCaseAndJobFieldContainingIgnoreCase(String address, String jobField, Pageable pageable);

    // Tìm tất cả bài đăng theo nhân viên đã tạo
    Page<JobPosting> findByEmployee(Employee employee, Pageable pageable);

    @Modifying
    @Query("UPDATE JobPosting j SET j.status = 'EXPIRED' WHERE j.endDate < :now AND j.status = 'ACTIVE'")
    void updateExpiredJobs(@Param("now") LocalDateTime now);

}
