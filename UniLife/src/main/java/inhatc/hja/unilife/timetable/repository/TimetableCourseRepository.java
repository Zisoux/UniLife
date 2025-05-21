package inhatc.hja.unilife.timetable.repository;

import inhatc.hja.unilife.timetable.entity.TimetableCourse;

=======
>>>>>>> tmp-timetable
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
=======
import inhatc.hja.unilife.timetable.entity.TimetableCourse;

import java.util.List;

public interface TimetableCourseRepository extends JpaRepository<TimetableCourse, Long> {

    List<TimetableCourse> findByTimetableUserIdAndTimetableSemester(Long userId, String semester);

}
