package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int status;
    private String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

    private String token;
    private String userRole;
    private String expirationTime;

    private int totalPage;
    private long totalElements;

    private UserDto userDto;
    private List<UserDto> userDtoList;

    private JobPostingDto jobPostingDto;
    private List<JobPostingDto> jobPostingDtoList;

    private SampleCVDto sampleCVDto;
    private List<SampleCVDto> sampleCVDtoList;

    private PolicyDto policyDto;
    private List<PolicyDto> policyDtoList;

    private JobApplicationDto jobApplicationDto;
    private List<JobApplicationDto> jobApplicationDtoList;

    private CandidateFavoriteJobDto candidateFavoriteJobDto;
    private List<CandidateFavoriteJobDto> candidateFavoriteJobDtoList;
}
