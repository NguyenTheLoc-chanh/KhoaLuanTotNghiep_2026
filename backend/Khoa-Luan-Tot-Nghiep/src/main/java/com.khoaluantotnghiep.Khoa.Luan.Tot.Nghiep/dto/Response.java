package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int status;
    private String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

    private String token;
    private String refreshToken;
    private List<String> roles;
    private String expirationTime;
    private String googleAuthUrl;

    private Integer currentPage;
    private Long totalItems;
    private Integer totalPages;

    private String shareLinkJob;
    private String shareLinkCV;

    private Map<String, Object> statistics;
    private List<JobCategoryStatsDto> jobCategoryStats;

    private UserDto userDto;
    private List<UserDto> userDtoList;

    private CandidateDto candidateDto;
    private List<CandidateDto> candidateDtoList;

    private EmployeeDto employerDto;
    private List<EmployeeDto> employerDtoList;

    private JobPostingDto jobPostingDto;
    private List<JobPostingDto> jobPostingDtoList;

    private JobCategoryDto jobCategoryDto;
    private List<JobCategoryDto> jobCategoryDtoList;

    private SampleCVDto sampleCVDto;
    private List<SampleCVDto> sampleCVDtoList;

    private PolicyDto policyDto;
    private List<PolicyDto> policyDtoList;

    private FeedbackDto feed;
    private List<FeedbackDto> feedbackDtoList;

    private JobApplicationDto jobApplicationDto;
    private List<JobApplicationDto> jobApplicationDtoList;

    private CandidateFavoriteJobDto candidateFavoriteJobDto;
    private List<CandidateFavoriteJobDto> candidateFavoriteJobDtoList;
}
