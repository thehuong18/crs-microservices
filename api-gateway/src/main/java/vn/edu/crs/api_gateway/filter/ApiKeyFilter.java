package vn.edu.crs.apigateway.filter;
import vn.edu.crs.apigateway.cache.ApiKeyValidationCache;
import vn.edu.crs.apigateway.client.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ApiKeyFilter implements GlobalFilter, Ordered {

    private final AuthServiceClient authServiceClient;
    private final ApiKeyValidationCache cache;

    private static final String PARTNER_PATH = "/api/public/courses";
    private static final String REQUIRED_SCOPE = "courses:read";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Chỉ kiểm tra X-API-KEY đối với endpoint công khai cho đối tác
        if (!path.startsWith(PARTNER_PATH)) {
            return chain.filter(exchange);
        }

        String apiKey = request.getHeaders().getFirst("X-API-KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return reject(exchange);
        }

        String cacheKey = apiKey + ":" + REQUIRED_SCOPE;
        Boolean cached = cache.get(cacheKey);
        if (cached != null) {
            return cached ? chain.filter(exchange) : reject(exchange);
        }

        return authServiceClient.isValidForScope(apiKey, REQUIRED_SCOPE)
                .defaultIfEmpty(false)   // Trả về false nếu auth-service trả kết quả rỗng
                .onErrorReturn(false)    // Fail-safe: Trả về false nếu auth-service bị ngắt kết nối
                .flatMap(valid -> {
                    cache.put(cacheKey, valid);
                    return valid ? chain.filter(exchange) : reject(exchange);
                });
    }

    private Mono<Void> reject(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -2;
    }
}