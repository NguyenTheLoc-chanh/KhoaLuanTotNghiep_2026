package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.PolicyDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Policy;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.PolicyMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.PolicyRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {
    private final PolicyRepo policyRepo;
    private final PolicyMapper policyMapper;

    @Override
    public Response createPolicy(PolicyDto policyDto) {
        Policy policy = new Policy();
        policy.setTitle(policyDto.getTitle());
        policy.setDescription(policyDto.getDescription());
        Policy saved = policyRepo.save(policy);

        PolicyDto savedDto = policyMapper.toDto(saved);
        return Response.builder()
                .status(201)
                .message("Tạo chính sách thành công")
                .policyDto(savedDto)
                .build();
    }

    @Override
    public Response getAllPolicies(int page, int size) {
        if (page <= 0) page = 0;
        if (size <= 0) size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("policyId").descending());
        Page<Policy> policyPage = policyRepo.findAll(pageable);

        List<PolicyDto> policyDtos = policyPage.getContent().stream()
                .map(policyMapper::toDto)
                .toList();
        if (policyDtos.isEmpty()) {
            throw new ResourceNotFoundException("Không có chính sách nào");
        }
        return Response.builder()
                .status(200)
                .message("Get all users successfully")
                .policyDtoList(policyDtos)
                .currentPage(policyPage.getNumber())
                .totalItems(policyPage.getTotalElements())
                .totalPages(policyPage.getTotalPages())
                .build();
    }

    @Override
    public Response getPolicyById(Long id) {
        Policy policy = policyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chính sách với id = " + id));
        PolicyDto policyDto = policyMapper.toDto(policy);
        return Response.builder()
                .status(200)
                .message("Lấy chính sách thành công")
                .policyDto(policyDto)
                .build();
    }

    @Override
    public Response updatePolicy(Long id, PolicyDto policyDto) {
        Policy policy = policyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chính sách với id = " + id));
        policy.setTitle(policyDto.getTitle());
        policy.setDescription(policyDto.getDescription());
        Policy updated = policyRepo.save(policy);
        PolicyDto updatedDto = policyMapper.toDto(updated);
        return Response.builder()
                .status(200)
                .message("Cập nhật chính sách thành công")
                .policyDto(updatedDto)
                .build();
    }

    @Override
    public Response deletePolicy(Long id) {
        Policy policy = policyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chính sách với id = " + id));
        policyRepo.delete(policy);

        return Response.builder()
                .status(200)
                .message("Xóa chính sách thành công!")
                .build();
    }
}
