package inhatc.hja.unlife.timetable.repository;

import inhatc.hja.unlife.timetable.entity.TimetableCourse;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableCourseRepository extends JpaRepository<TimetableCourse, Long> {

    List<TimetableCourse> findByTimetableUserIdAndTimetableSemester(Long userId, String semester);

}

