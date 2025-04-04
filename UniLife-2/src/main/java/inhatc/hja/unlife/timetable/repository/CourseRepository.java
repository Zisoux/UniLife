package inhatc.hja.unlife.timetable.repository;



import inhatc.hja.unlife.timetable.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
