package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.Role;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.User;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity.UserRole;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.exception.ConflictException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtUtils {

    private static final long EXPIRATION_TIME = 1000L * 60L * 60L * 24L; // 1 ngày
    private SecretKey secretKey;

    @Value("${secreteJwtString}")
    private String secreteJwtString; // Make sure the value in the application property is 32 characters or long

    @PostConstruct
    private void init(){
        byte[] keyBytes = secreteJwtString.getBytes();
        this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = Optional.ofNullable(user.getUserRoles())
                .orElse(Collections.emptyList())
                .stream()
                .map(UserRole::getRole)
                .map(Role::getRoleName)
                .collect(Collectors.toList());
        claims.put("roles", roles); // lưu quyền vào token

        return buildToken(claims, user.getEmail());
    }

    public String buildToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }
    // Tạo token để reset password, thời gian sống ngắn 5 phút
    public String generateResetPasswordToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim("type", "RESET_PASSWORD")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000)) // 5 phút
                .signWith(secretKey)
                .compact();
    }
    public String validateResetPasswordToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)             // thay cho setSigningKey
                .build()
                .parseSignedClaims(token)          // thay cho parseClaimsJws
                .getPayload();
        if (!"RESET_PASSWORD".equals(claims.get("type", String.class))) {
            throw new ConflictException("Token không hợp lệ");
        }
        return claims.getSubject(); // return email
    }

    public String getUserNameFromToken(String token){
        return extractClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return (List<String>) claims.get("roles", List.class);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isValidateToken(String token, UserDetails userDetails){
        final String username = getUserNameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    public Date getExpirationFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
