package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.specification;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.JobPosting;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.JobPostingStatus;
import org.springframework.data.jpa.domain.Specification;

public final class JobPostingSpecification {

    private JobPostingSpecification() {
        // Utility class: ngăn không cho new
    }

    public static Specification<JobPosting> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return null; // return null để Specification API tự bỏ qua
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<JobPosting> hasCompanyName(String companyName) {
        return (root, query, cb) -> {
            if (companyName == null || companyName.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.join("employee").get("companyName")), "%" + companyName.toLowerCase() + "%");
        };
    }

    public static Specification<JobPosting> hasAddress(String address) {
        return (root, query, cb) -> {
            if (address == null || address.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("address")), "%" + address.toLowerCase() + "%");
        };
    }

    public static Specification<JobPosting> salaryBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) {
                return cb.between(root.get("salaryMin"), min, max);
            }
            if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("salaryMin"), min);
            }
            return cb.lessThanOrEqualTo(root.get("salaryMax"), max);
        };
    }
    public static Specification<JobPosting> hasJobCategoryName(String jobCategoryName) {
        return (root, query, cb) -> {
            if (jobCategoryName == null || jobCategoryName.isBlank()) {
                return null;
            }

            // JOIN tới bảng JobCategory
            return cb.like(
                    cb.lower(root.join("jobCategory").get("name")),
                    "%" + jobCategoryName.toLowerCase() + "%"
            );
        };
    }


    public static Specification<JobPosting> isActive() {
        return (root, query, cb) -> cb.equal(root.get("status"), JobPostingStatus.ACTIVE);
    }
}
