package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.CandidateDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import org.springframework.web.multipart.MultipartFile;

public interface CandidateService {
    Response createCandidate(CandidateDto candidateDto);
    Response getCandidateByUserId(Long userId);
    Response updateCandidateInfo(CandidateDto candidateDto);
    Response updateCandidateAvatar(Long userId, MultipartFile avatarFile);
    Response updateCandidateFCv(Long userId, MultipartFile fCvFile);
    Response deleteCandidate(Long userId);
    Response getAllCandidates(int page, int size, String sortBy, String sortDir, String search);
}
