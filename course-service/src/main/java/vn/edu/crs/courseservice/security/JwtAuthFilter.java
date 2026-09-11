package vn.edu.crs.courseservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

                // Sử dụng cú pháp phân giải JWT mới nhất của JJWT 0.12.x
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                Long userId = claims.get("userId", Long.class); // Đọc luôn userId sớm để phục vụ buổi 9

                var authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        userId, // Đưa userId vào phần credentials để Controller dễ dàng lấy ra
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                // Lưu thông tin người dùng vào Security Context để kích hoạt phân quyền
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                SecurityContextHolder.clearContext(); // Token lỗi hoặc hết hạn -> xóa sạch ngữ cảnh bảo mật
            }
        }
        filterChain.doFilter(request, response);
    }
}