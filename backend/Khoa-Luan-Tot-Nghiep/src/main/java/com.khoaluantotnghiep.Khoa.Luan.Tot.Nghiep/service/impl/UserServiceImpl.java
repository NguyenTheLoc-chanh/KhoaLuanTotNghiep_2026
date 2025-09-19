package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.RoleUser;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ConflictException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ResourceNotFoundException;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.UserMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.*;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.JwtUtils;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.CloudinaryService;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private final RefreshTokenServiceImpl refreshTokenService;
    private final JwtUtils jwtUtils;
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
    public Response loginUser(LoginRequest loginRequest) {
        User user = userRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với email: " + loginRequest.getEmail()));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Mật khẩu không chính xác");
        }
        String token = jwtUtils.generateToken(user);
        // Lấy thời gian hết hạn token -> ISO 8601
        String expirationStr = jwtUtils.getExpirationFromToken(token)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        // Lấy roles từ user
        List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleName())
                .toList();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return Response.builder()
                .status(200)
                .message("Đăng nhập thành công!")
                .token(token)
                .refreshToken(refreshToken.getToken())
                .expirationTime(expirationStr)
                .roles(roles)
                .userDto(userMapper.toDto(user))
                .build();
    }

    @Override
    public Response getAllUsers(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
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

    @Override
    public User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Principal: " + authentication.getPrincipal());
        System.out.println("Authorities: " + authentication.getAuthorities());
        // Kiểm tra xem user đã đăng nhập hợp lệ chưa
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Bạn chưa đăng nhập hoặc phiên đã hết hạn");
        }
        String email = authentication.getName();

        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));
    }

    @Override
    public Response logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Bạn chưa đăng nhập hoặc phiên đã hết hạn");
        }

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Email: " + email));

        refreshTokenService.revokeToken(user);

        return Response.builder()
                .status(200)
                .message("Logout thành công!")
                .build();
    }
}
