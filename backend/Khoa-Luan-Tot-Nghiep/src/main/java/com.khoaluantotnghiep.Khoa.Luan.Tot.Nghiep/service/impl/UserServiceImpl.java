package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.AdminRegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.CandidateRegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.EmployerRegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.RegisterRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.RoleUser;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ConflictException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.UserMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.CloudinaryService;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final UserRoleRepo userRoleRepo;
    private final EmployeeRepo employeeRepo;
    private final CandidateRepo candidateRepo;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final UserMapper userMapper;

    @Override
    public User saveUserWithRole(RegisterRequest request, RoleUser roleEnum) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already registered");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ConflictException("Mật khẩu và xác nhận mật khẩu không trùng khớp");
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepo.save(user);
        Role role = roleRepo.findByRoleName(roleEnum.name())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role: " + roleEnum.name()));
        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(role);
        userRoleRepo.save(userRole);

        return userRepo.findById(savedUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found after save"));
    }

    @Override
    @Transactional
    public Response registerCandidate(CandidateRegisterRequest registrationCandidateRequest) {
        log.info("Registering candidate: {}", registrationCandidateRequest);
        User savedUser = saveUserWithRole(registrationCandidateRequest, RoleUser.CANDIDATE);
        // Nếu là ứng viên
        if (RoleUser.CANDIDATE.name().equalsIgnoreCase(registrationCandidateRequest.getRole())) {
            Candidate candidate = new Candidate();
            candidate.setUser(savedUser);
            candidateRepo.save(candidate);
        }

        return Response.builder()
                .status(201)
                .message("Đăng ký thành công ứng viên!")
                .userDto(userMapper.toDto(savedUser))
                .build();
    }

    @Override
    @Transactional
    public Response registerEmployer(EmployerRegisterRequest registrationRequest) {
        User savedUser = saveUserWithRole(registrationRequest, RoleUser.EMPLOYER);
        // Nếu là nhà tuyển dụng thì set thêm businessLicense
        Employee employee = new Employee();
        employee.setUser(savedUser);
        if (registrationRequest.getBusinessLicense() != null && !registrationRequest.getBusinessLicense().isEmpty()) {
            // Upload file PDF lên Cloudinary
            String pdfUrl = cloudinaryService.uploadPdf(registrationRequest.getBusinessLicense());
            employee.setBusinessLicense(pdfUrl);
        } else {
            employee.setBusinessLicense(null);
        }
        employeeRepo.save(employee);

        return Response.builder()
                .status(201)
                .message("Đăng ký thành công nhà tuyển dụng!")
                .userDto(userMapper.toDto(savedUser))
                .build();
    }

    @Override
    public Response registerAdmin(AdminRegisterRequest registrationRequest) {
        User savedUser = saveUserWithRole(registrationRequest, RoleUser.ADMIN);
        return Response.builder()
                .status(201)
                .message("Đăng ký thành công admin!")
                .userDto(userMapper.toDto(savedUser))
                .build();
    }


    @Override
    public Response getAllUsers(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").descending());
        Page<User> usersPage = userRepo.findAll(pageable);

        List<UserDto> userDtos = usersPage.getContent().stream()
                .map(userMapper::toDto)
                .toList();
        if (userDtos.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return Response.builder()
                .status(200)
                .message("Get all users successfully")
                .userDtoList(userDtos)
                .currentPage(usersPage.getNumber())
                .totalItems(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .build();
    }
}
