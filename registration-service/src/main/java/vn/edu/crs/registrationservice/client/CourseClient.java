package vn.edu.crs.registrationservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CourseClient {

    private final RestTemplate restTemplate;

    @Value("${course-service.base-url}")
    private String courseServiceBaseUrl;

    // Gọi reserve-seat sang course-service
    public void reserveSeat(Long courseId) {
        String url = courseServiceBaseUrl + "/internal/courses/" + courseId + "/reserve-seat";
        try {
            restTemplate.exchange(url, HttpMethod.PATCH, null, Void.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                if (e.getStatusCode().value() == 404) {
                    throw new IllegalArgumentException("Mon hoc khong ton tai");
                } else if (e.getStatusCode().value() == 409) {
                    // Giữ nguyên message từ course-service
                    throw new IllegalStateException("Mon hoc da het cho, khong the dang ky");
                }
            }
            // Các lỗi khác (5xx, connection...)
            throw new IllegalArgumentException("Khong the ket noi toi course-service, vui long thu lai sau");
        } catch (Exception e) {
            throw new IllegalArgumentException("Khong the ket noi toi course-service, vui long thu lai sau");
        }
    }

    // Gọi release-seat sang course-service
    public void releaseSeat(Long courseId) {
        String url = courseServiceBaseUrl + "/internal/courses/" + courseId + "/release-seat";
        try {
            restTemplate.exchange(url, HttpMethod.PATCH, null, Void.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Mon hoc khong ton tai");
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new IllegalArgumentException("Khong the ket noi toi course-service, vui long thu lai sau");
        }
    }
}