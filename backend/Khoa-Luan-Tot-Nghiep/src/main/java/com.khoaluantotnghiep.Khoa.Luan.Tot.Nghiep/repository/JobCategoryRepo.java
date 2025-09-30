package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobCategoryStatsDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobCategoryRepo extends JpaRepository<JobCategory,Long> {
    @Query("SELECT new com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.JobCategoryStatsDto(c.name, COUNT(j)) " +
            "FROM JobCategory c " +
            "LEFT JOIN c.jobPostings j ON j.status = com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobPostingStatus.ACTIVE " +
            "GROUP BY c.name " +
            "ORDER BY COUNT(j) DESC")
    List<JobCategoryStatsDto> getJobCategoryStats();
}
