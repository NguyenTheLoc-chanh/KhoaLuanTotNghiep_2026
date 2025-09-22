package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUser_UserId(Long userId);
    Page<Employee> findByUser_FullNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Employee> findByCompanyNameContainingIgnoreCase(String keyword, Pageable pageable);
}
