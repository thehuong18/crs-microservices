package vn.edu.crs.courseservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.edu.crs.courseservice.security.JwtAuthFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/internal/**").permitAll() // Cho phép registration-service gọi ngầm qua mạng nội bộ
                        .requestMatchers(HttpMethod.GET, "/courses/**").permitAll() // Public cho mọi người xem danh sách
                        .requestMatchers(HttpMethod.POST, "/courses/**").hasRole("ADMIN") // Thêm môn học phải là ADMIN
                        .requestMatchers(HttpMethod.PUT, "/courses/**").hasRole("ADMIN")  // Sửa môn học phải là ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/courses/**").hasRole("ADMIN") // Xóa môn học phải là ADMIN
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}