package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.PolicyDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;

public interface PolicyService {
    Response createPolicy(PolicyDto policyDto);
    Response getAllPolicies(int page, int size);
    Response getPolicyById(Long id);
    Response updatePolicy(Long id, PolicyDto policyDto);
    Response deletePolicy(Long id);
}
