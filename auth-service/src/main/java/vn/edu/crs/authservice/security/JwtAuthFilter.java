package vn.edu.crs.authservice.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String username = request.getHeader("X-User-Name");
        String role = request.getHeader("X-User-Role");

        if (username != null && role != null) {
            // Chuẩn hóa tên role (loại bỏ tiền tố ROLE_ nếu có)
            String rawRole = role.replace("ROLE_", "");

            // Cấp cả 2 dạng "ADMIN" và "ROLE_ADMIN" vào SecurityContext
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(rawRole),
                    new SimpleGrantedAuthority("ROLE_" + rawRole)
            );

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username, null, authorities
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}