package vn.edu.crs.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthHeaderFilter implements GlobalFilter, Ordered {

    private static final List<String> OPEN_PATHS = List.of(
            "/api/auth/login",
            "/api/public/courses"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. Kiểm tra các đường dẫn công khai (Public Paths)
        boolean isOpen = OPEN_PATHS.stream().anyMatch(path::startsWith);
        boolean isPublicCourseRead = path.startsWith("/api/courses") && "GET".equalsIgnoreCase(request.getMethod().name());

        if (isOpen || isPublicCourseRead) {
            return chain.filter(exchange);
        }

        // 2. Kiểm tra Authorization Header (Dùng getFirst kiểm tra null hoặc rỗng)
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isBlank()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}