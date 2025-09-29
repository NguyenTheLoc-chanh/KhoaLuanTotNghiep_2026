package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.SampleCVDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.SampleCVRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.SampleCV;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.SampleCVMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.SampleCVRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.UserRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.CloudinaryService;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.SampleCVService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SampleCVServiceImpl implements SampleCVService {

    private final SampleCVRepo sampleCVRepo;
    private final UserRepo userRepo;
    private final SampleCVMapper sampleCVMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    public Response createSampleCV(Long userId, SampleCVRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với id = " + userId));

        SampleCV sampleCV = new SampleCV();
        sampleCV.setTitle(request.getTitle());
        sampleCV.setDescription(request.getDescription());
        if (request.getFCvFileFormat() != null && !request.getFCvFileFormat().isEmpty()) {
            String fileUrl = cloudinaryService.uploadPdf(request.getFCvFileFormat());
            sampleCV.setFCvFileFormat(fileUrl);
        }
        sampleCV.setUser(user);

        SampleCV saved = sampleCVRepo.save(sampleCV);

        return Response.builder()
                .status(201)
                .message("Tạo mẫu CV thành công!")
                .sampleCVDto(sampleCVMapper.toDto(saved))
                .build();
    }

    @Override
    public Response getAllSampleCVs(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<SampleCV> sampleCVPage;

        if (search != null && !search.trim().isEmpty()) {
            // Nếu muốn tìm kiếm theo title
            sampleCVPage = sampleCVRepo.findByTitleContainingIgnoreCase(search, pageable);
        } else {
            sampleCVPage = sampleCVRepo.findAll(pageable);
        }

        List<SampleCVDto> sampleCVDtos = sampleCVPage.getContent()
                .stream()
                .map(sampleCVMapper::toDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("Lấy danh sách SampleCV thành công")
                .sampleCVDtoList(sampleCVDtos)
                .currentPage(sampleCVPage.getNumber())
                .totalItems(sampleCVPage.getTotalElements())
                .totalPages(sampleCVPage.getTotalPages())
                .build();
    }

    @Override
    public Response getSampleCVById(Long sampleCVId) {
        SampleCV sampleCV = sampleCVRepo.findById(sampleCVId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy SampleCV với id = " + sampleCVId));

        return Response.builder()
                .status(200)
                .message("Lấy thông tin SampleCV thành công")
                .sampleCVDto(sampleCVMapper.toDto(sampleCV))
                .build();
    }

    @Override
    public Response updateSampleCV(Long sampleCVId, SampleCVRequest request) {
        SampleCV sampleCV = sampleCVRepo.findById(sampleCVId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy SampleCV với id = " + sampleCVId));

        sampleCV.setTitle(request.getTitle());
        sampleCV.setDescription(request.getDescription());
        if (request.getFCvFileFormat() != null && !request.getFCvFileFormat().isEmpty()) {
            String fileUrl = cloudinaryService.uploadPdf(request.getFCvFileFormat());
            sampleCV.setFCvFileFormat(fileUrl);
        }

        SampleCV updated = sampleCVRepo.save(sampleCV);

        return Response.builder()
                .status(200)
                .message("Cập nhật SampleCV thành công")
                .sampleCVDto(sampleCVMapper.toDto(updated))
                .build();
    }

    @Override
    public Response deleteSampleCV(Long sampleCVId) {
        SampleCV sampleCV = sampleCVRepo.findById(sampleCVId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy SampleCV với id = " + sampleCVId));

        sampleCVRepo.delete(sampleCV);

        return Response.builder()
                .status(200)
                .message("Xóa SampleCV thành công")
                .build();
    }
}
