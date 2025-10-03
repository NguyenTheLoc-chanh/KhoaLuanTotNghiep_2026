package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDto {
    private String fullName;
    private Long candidateId;
    private String avatar;
    private Integer experienceYear;
    private String fCv;

    private Long userId;
    private String status;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
}
