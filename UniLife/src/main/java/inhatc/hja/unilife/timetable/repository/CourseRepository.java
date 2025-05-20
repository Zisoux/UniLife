package inhatc.hja.unilife.timetable.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import inhatc.hja.unilife.timetable.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
