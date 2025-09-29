package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.SampleCV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleCVRepo extends JpaRepository<SampleCV, Long> {
    Page<SampleCV> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
