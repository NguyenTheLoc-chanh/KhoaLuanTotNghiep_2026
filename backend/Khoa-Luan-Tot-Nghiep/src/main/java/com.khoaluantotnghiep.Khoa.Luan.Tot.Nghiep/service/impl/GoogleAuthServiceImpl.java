package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request.GoogleLoginRequest;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.Response;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Candidate;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Employee;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Role;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.UserRole;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.RoleUser;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.enums.UserStatus;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.mapper.UserMapper;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.CandidateRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.EmployeeRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.RoleRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.UserRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.repository.UserRoleRepo;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security.JwtUtils;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.interf.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final UserRoleRepo userRoleRepo;
    private final CandidateRepo candidateRepo;
    private final EmployeeRepo employeeRepo;
    private final JwtUtils jwtUtils;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Override
    public Response authenticateWithGoogle(GoogleLoginRequest request) {
        try {
            // Verify Google ID token
            GoogleUserInfo userInfo = verifyGoogleIdToken(request.getIdToken());
            
            if (userInfo == null) {
                return Response.builder()
                        .status(400)
                        .message("ID token không hợp lệ hoặc đã hết hạn")
                        .build();
            }

            // Tìm hoặc tạo user
            User user = findOrCreateUser(userInfo, request.getRole());

            // Xử lý đăng nhập lại - vô hiệu hóa refresh token cũ
            handleReLogin(user);

            // Tạo JWT token
            String token = jwtUtils.generateToken(user);
            String expirationStr = jwtUtils.getExpirationFromToken(token)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            // Tạo refresh token mới
            var refreshToken = refreshTokenService.createRefreshToken(user);

            // Lấy roles
            List<String> roles = Optional.ofNullable(user.getUserRoles())
                    .orElse(List.of())
                    .stream()
                    .map(ur -> ur.getRole().getRoleName())
                    .toList();


            log.info("Đăng nhập Google thành công cho user: {} - Role: {}", 
                user.getEmail(), roles);

            return Response.builder()
                    .status(200)
                    .message("Đăng nhập Google thành công!")
                    .token(token)
                    .refreshToken(refreshToken.getToken())
                    .expirationTime(expirationStr)
                    .roles(roles)
                    .userDto(userMapper.toDto(user))
                    .build();

        } catch (Exception e) {
            log.error("Lỗi khi đăng nhập Google: ", e);
            return Response.builder()
                    .status(400)
                    .message("Đăng nhập Google thất bại: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response getGoogleAuthUrl(String role) {
        String googleAuthUrl = String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=email%%20profile&state=%s",
                googleClientId, redirectUri, role
        );

        return Response.builder()
                .status(200)
                .message("Google OAuth2 URL")
                .googleAuthUrl(googleAuthUrl)
                .build();
    }

    @Override
    public Response handleGoogleCallback(String code, String state) {
        try {
            if (code == null) {
                return Response.builder()
                        .status(400)
                        .message("Authorization code không được cung cấp")
                        .build();
            }

            // Exchange code for access token
            String accessToken = exchangeCodeForAccessToken(code);
            if (accessToken == null) {
                return Response.builder()
                        .status(400)
                        .message("Không thể lấy access token từ Google")
                        .build();
            }

            // Get user info from Google
            GoogleUserInfo userInfo = getUserInfoFromGoogle(accessToken);
            if (userInfo == null) {
                return Response.builder()
                        .status(400)
                        .message("Không thể lấy thông tin user từ Google")
                        .build();
            }

            // Tìm hoặc tạo user
            String role = state != null ? state : "CANDIDATE";
            User user = findOrCreateUser(userInfo, role);

            // Xử lý đăng nhập lại - vô hiệu hóa refresh token cũ
            handleReLogin(user);

            // Tạo JWT token
            String token = jwtUtils.generateToken(user);
            String expirationStr = jwtUtils.getExpirationFromToken(token)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            // Tạo refresh token mới
            var refreshToken = refreshTokenService.createRefreshToken(user);

            // Lấy roles
            List<String> roles = Optional.ofNullable(user.getUserRoles())
                    .orElse(List.of())
                    .stream()
                    .map(ur -> ur.getRole().getRoleName())
                    .toList();


            return Response.builder()
                    .status(200)
                    .message("Đăng nhập Google thành công!")
                    .token(token)
                    .refreshToken(refreshToken.getToken())
                    .expirationTime(expirationStr)
                    .roles(roles)
                    .userDto(userMapper.toDto(user))
                    .build();

        } catch (Exception e) {
            log.error("Lỗi khi xử lý Google callback: ", e);
            return Response.builder()
                    .status(400)
                    .message("Xử lý Google callback thất bại: " + e.getMessage())
                    .build();
        }
    }

    private GoogleUserInfo verifyGoogleIdToken(String idToken) {
        try {
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.getBody());
                
                return GoogleUserInfo.builder()
                        .sub(jsonNode.get("sub").asText())
                        .email(jsonNode.get("email").asText())
                        .name(jsonNode.get("name").asText())
                        .picture(jsonNode.get("picture").asText())
                        .emailVerified(jsonNode.get("email_verified").asBoolean())
                        .build();
            }
        } catch (Exception e) {
            log.error("Lỗi khi verify Google ID token: ", e);
        }
        return null;
    }

    @Override
    public String exchangeCodeForAccessToken(String code) {
        try {
            String url = "https://oauth2.googleapis.com/token";
            String requestBody = String.format(
                    "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s",
                    googleClientId, googleClientSecret, code, redirectUri
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded");
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.getBody());
                return jsonNode.get("access_token").asText();
            }
        } catch (Exception e) {
            log.error("Lỗi khi exchange code for access token: ", e);
        }
        return null;
    }

    private GoogleUserInfo getUserInfoFromGoogle(String accessToken) {
        try {
            String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.getBody());
                
                return GoogleUserInfo.builder()
                        .sub(jsonNode.get("id").asText())
                        .email(jsonNode.get("email").asText())
                        .name(jsonNode.get("name").asText())
                        .picture(jsonNode.get("picture").asText())
                        .emailVerified(jsonNode.get("verified_email").asBoolean())
                        .build();
            }
        } catch (Exception e) {
            log.error("Lỗi khi lấy user info từ Google: ", e);
        }
        return null;
    }

    private User findOrCreateUser(GoogleUserInfo userInfo, String role) {
        Optional<User> existingUser = userRepo.findByEmail(userInfo.getEmail());
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            String currentRole = user.getUserRoles().isEmpty() ? "Chưa có role" : 
                user.getUserRoles().get(0).getRole().getRoleName();
            
            log.info("User đã tồn tại: {} - Role hiện tại: {} - Role yêu cầu: {}", 
                user.getEmail(), currentRole, role);
            
            // Cập nhật thông tin user nếu có thay đổi
            updateUserInfoIfNeeded(user, userInfo);
            
            // Kiểm tra xem user có muốn đổi role không
            handleRoleChangeIfNeeded(user, role);
            
            return user;
        }

        // Tạo user mới
        User newUser = User.builder()
                .email(userInfo.getEmail())
                .fullName(userInfo.getName())
                .password("GOOGLE_OAUTH2_USER")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        User savedUser = userRepo.save(newUser);

        // Tạo role cho user
        RoleUser roleEnum = "EMPLOYER".equalsIgnoreCase(role) ? RoleUser.EMPLOYER : RoleUser.CANDIDATE;
        Role roleEntity = roleRepo.findByRoleName(roleEnum.name())
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleEnum.name()));

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(roleEntity);

        savedUser.getUserRoles().add(userRole);

        userRoleRepo.save(userRole);

        // Tạo Candidate hoặc Employee
        if (roleEnum == RoleUser.CANDIDATE) {
            Candidate candidate = new Candidate();
            candidate.setUser(savedUser);
            candidateRepo.save(candidate);
            log.info("Đã tạo Candidate profile cho user: {}", savedUser.getEmail());
        } else {
            Employee employee = new Employee();
            employee.setUser(savedUser);
            employee.setBusinessLicense(null);
            employeeRepo.save(employee);
            log.info("Đã tạo Employee profile cho user: {}", savedUser.getEmail());
        }

        return savedUser;
    }

    /**
     * Xử lý đăng nhập lại - vô hiệu hóa refresh token cũ và tạo mới
     */
    @Override
    public void handleReLogin(User user) {
        try {
            // Vô hiệu hóa tất cả refresh token cũ của user
            refreshTokenService.revokeToken(user);
            log.info("Đã vô hiệu hóa refresh token cũ cho user: {}", user.getEmail());
        } catch (Exception e) {
            log.warn("Không thể vô hiệu hóa refresh token cũ cho user {}: {}", 
                user.getEmail(), e.getMessage());
        }
    }

    /**
     * Xử lý việc đổi role nếu user muốn đổi
     */
    @Override
    public void handleRoleChangeIfNeeded(User user, String requestedRole) {
        if (user.getUserRoles().isEmpty()) {
            log.info("User {} chưa có role, sẽ tạo role: {}", user.getEmail(), requestedRole);
            return;
        }

        String currentRole = user.getUserRoles().get(0).getRole().getRoleName();
        
        if (!currentRole.equals(requestedRole)) {
            log.info("User {} muốn đổi role từ {} sang {}", 
                user.getEmail(), currentRole, requestedRole);
            
            // Hiện tại chỉ log, có thể mở rộng để xử lý đổi role
            // Trong tương lai có thể implement logic đổi role ở đây
            log.warn("Chức năng đổi role chưa được implement. User {} giữ nguyên role: {}", 
                user.getEmail(), currentRole);
        }
    }

    /**
     * Cập nhật thông tin user nếu có thay đổi từ Google
     */
    private void updateUserInfoIfNeeded(User user, GoogleUserInfo userInfo) {
        boolean needUpdate = false;
        
        // Cập nhật fullName nếu có thay đổi
        if (!userInfo.getName().equals(user.getFullName())) {
            user.setFullName(userInfo.getName());
            needUpdate = true;
            log.info("Cập nhật fullName cho user {}: {} -> {}", 
                user.getEmail(), user.getFullName(), userInfo.getName());
        }
        
        // Cập nhật updatedAt
        if (needUpdate) {
            user.setUpdatedAt(LocalDateTime.now());
            userRepo.save(user);
            log.info("Đã cập nhật thông tin user: {}", user.getEmail());
        }
    }

    // Inner class để lưu thông tin user từ Google
    @lombok.Data
    @lombok.Builder
    private static class GoogleUserInfo {
        private String sub;
        private String email;
        private String name;
        private String picture;
        private boolean emailVerified;
    }
}
