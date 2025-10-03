package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.CandidateDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Candidate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CandidateMapper {
    private final ModelMapper modelMapper;

    public CandidateMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CandidateDto toDto(Candidate candidate) {
        CandidateDto dto = modelMapper.map(candidate, CandidateDto.class);
        if (candidate.getUser() != null) {
            dto.setUserId(candidate.getUser().getUserId());
            dto.setFullName(candidate.getUser().getFullName());
            dto.setStatus(candidate.getUser().getStatus().toString());
            dto.setEmail(candidate.getUser().getEmail());
            dto.setPhone(candidate.getUser().getPhone());
            dto.setCreatedAt(candidate.getUser().getCreatedAt());
        }
        return dto;
    }

    public Candidate toEntity(CandidateDto dto) {
        return modelMapper.map(dto, Candidate.class);
    }
}
