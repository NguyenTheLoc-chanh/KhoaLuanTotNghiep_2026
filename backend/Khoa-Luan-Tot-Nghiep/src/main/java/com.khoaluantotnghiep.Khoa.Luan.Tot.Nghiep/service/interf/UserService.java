package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.AdminRegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.CandidateRegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.EmployerRegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.RegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.RoleUser;

public interface UserService {
    User saveUserWithRole(RegisterRequest request, RoleUser roleEnum);
    Response registerCandidate(CandidateRegisterRequest registrationRequest );
    Response registerEmployer(EmployerRegisterRequest registrationRequest );
    Response registerAdmin(AdminRegisterRequest registrationRequest );
    Response getAllUsers(int page, int size);
    Response getUserById(Long userId);
}
