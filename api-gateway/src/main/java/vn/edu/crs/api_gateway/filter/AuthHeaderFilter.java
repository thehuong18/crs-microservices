package vn.edu.crs.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthHeaderFilter implements GlobalFilter, Ordered {

    // Danh sách các URL được phép đi qua Gateway thoải mái không cần đăng nhập
    private static final List<String> OPEN_PATHS = List.of(
            "/api/auth/login",
            "/api/public/courses"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. Kiểm tra nếu nằm trong danh sách public
        boolean isOpen = OPEN_PATHS.stream().anyMatch(path::startsWith);

        // 2. Kiểm tra nếu là request GET xem danh sách môn học (sinh viên/khách đều xem được)
        boolean isPublicCourseRead = path.startsWith("/api/courses") &&
                request.getMethod().name().equals("GET");

        if (isOpen || isPublicCourseRead) {
            return chain.filter(exchange); // Cho phép đi qua tiếp
        }

        // 3. Nếu là các request cần bảo mật (POST/PUT/DELETE môn học, hoặc đăng ký học phần)
        if (!request.getHeaders().containsHeader("Authorization")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // Trả lỗi 401 ngay lập tức
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // Chạy sớm, ngay trước khi định tuyến đi tiếp
    }
}