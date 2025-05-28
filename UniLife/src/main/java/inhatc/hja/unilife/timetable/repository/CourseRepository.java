package inhatc.hja.unilife.timetable.repository;

import inhatc.hja.unilife.timetable.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
