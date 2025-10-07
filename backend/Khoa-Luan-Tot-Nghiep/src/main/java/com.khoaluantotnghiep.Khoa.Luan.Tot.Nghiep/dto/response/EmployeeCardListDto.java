package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCardListDto {
    private Long employeeId;
    private String companyName;
    private String companyLogo;
    private String address;
    private String scale;
    private String description;
}
