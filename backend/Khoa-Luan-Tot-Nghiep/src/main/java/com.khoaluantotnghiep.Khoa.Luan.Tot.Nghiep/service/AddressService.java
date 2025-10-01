package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.ProvinceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://provinces.open-api.vn/api/v1";

    /** Lấy tất cả tỉnh/thành */
    public List<ProvinceDto> getProvinces() {
        String url = BASE_URL + "/p";
        ResponseEntity<ProvinceDto[]> response = restTemplate.exchange(url, HttpMethod.GET, null, ProvinceDto[].class);
        return response.getBody() != null ? List.of(response.getBody()) : Collections.emptyList();
    }

    /** Lấy danh sách phường/xã theo tỉnh/thành */
    public List<ProvinceDto> getDistrictsByProvince(int provinceCode) {
        String url = BASE_URL + "/p/" + provinceCode + "?depth=2";
        ResponseEntity<ProvinceDto> response = restTemplate.exchange(url, HttpMethod.GET, null, ProvinceDto.class);
        return response.getBody() != null ? List.of(response.getBody()) : Collections.emptyList();
    }
}
