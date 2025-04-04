package inhatc.hja.unlife.timetable.repository;

import inhatc.hja.unlife.timetable.entity.TimetableCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableCourseRepository extends JpaRepository<TimetableCourse, Long> {
    // 필요하면 커스텀 메소드 추가 가능
}
