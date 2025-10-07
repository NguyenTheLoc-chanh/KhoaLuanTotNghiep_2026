package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobCategoryStatsDto {
    private Long jobCategoryId;
    private String categoryName;
    private Long jobCount;
}
