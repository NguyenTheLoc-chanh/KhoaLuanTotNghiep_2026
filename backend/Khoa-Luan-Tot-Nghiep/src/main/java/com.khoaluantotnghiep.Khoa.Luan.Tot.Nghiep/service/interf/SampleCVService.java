package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.SampleCVRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface SampleCVService {
    Response createSampleCV(Long userId, SampleCVRequest request);
    Response getAllSampleCVs(int page, int size, String sortBy, String sortDir, String search);
    Response getSampleCVById(Long sampleCVId);
    Response updateSampleCV(Long sampleCVId, SampleCVRequest request);
    Response deleteSampleCV(Long sampleCVId);
    ResponseEntity<Resource> downloadSampleCVFile(Long sampleCVId);
}
