package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Đánh dấu đây là class cấu hình của Spring (Spring sẽ load vào context).
@EnableWebSecurity //  Kích hoạt Spring Security cho ứng dụng web.
@EnableMethodSecurity // Cho phép sử dụng annotation bảo mật ở mức method
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable) // tắt CSRF (REST API không cần CSRF token)
                .cors(Customizer.withDefaults()) // bật CORS với cấu hình mặc định
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/auth/login", "/api/auth/register","/swagger-ui/**","/v3/api-docs/**")
                        .permitAll()// các endpoint này được truy cập không cần đăng nhập
                        .anyRequest().authenticated()) // các request khác cần đăng nhập
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // không tạo session, vì JWT sẽ được dùng để xác thực
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // gắn JwtAuthFilter vào trước UsernamePasswordAuthenticationFilter
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
