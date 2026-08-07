// path: course-service/src/main/java/vn/edu/crs/course_service/repository/CourseRepository.java

package vn.edu.crs.courseservice.repository;

import vn.edu.crs.courseservice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTenMonHocIgnoreCase(String tenMonHoc);
}