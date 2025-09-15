package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.CandidateFavoriteJobDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.CandidateFavoriteJob;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CandidateFavoriteJobMapper {
    private final ModelMapper modelMapper;

    public CandidateFavoriteJobMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CandidateFavoriteJobDto toDto(CandidateFavoriteJob entity) {
        CandidateFavoriteJobDto dto = modelMapper.map(entity, CandidateFavoriteJobDto.class);
        if (entity.getJobPosting() != null) {
            dto.setJobId(entity.getJobPosting().getJobId());
        }
        if (entity.getCandidate() != null) {
            dto.setCandidateId(entity.getCandidate().getCandidateId());
        }
        return dto;
    }

    public CandidateFavoriteJob toEntity(CandidateFavoriteJobDto dto) {
        return modelMapper.map(dto, CandidateFavoriteJob.class);
    }
}
