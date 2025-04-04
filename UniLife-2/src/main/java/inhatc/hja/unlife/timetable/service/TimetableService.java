package inhatc.hja.unlife.timetable.service;

import inhatc.hja.unlife.timetable.entity.Course;
import inhatc.hja.unlife.timetable.entity.Timetable;
import inhatc.hja.unlife.timetable.entity.TimetableCourse;
import inhatc.hja.unlife.timetable.repository.CourseRepository;
import inhatc.hja.unlife.timetable.repository.TimetableCourseRepository;
import inhatc.hja.unlife.timetable.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final TimetableCourseRepository timetableCourseRepository;
    private final CourseRepository courseRepository;

    public Timetable getTimetableWithCourses(Long userId) {
        return timetableRepository.findByUserId(userId);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public void addClassToTimetable(Long userId, Long courseId, String dayOfWeek, String startTime, String endTime) {
        Timetable timetable = timetableRepository.findByUserId(userId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID"));

        TimetableCourse timetableCourse = new TimetableCourse();
        timetableCourse.setTimetable(timetable);
        timetableCourse.setCourse(course);
        timetableCourse.setDayOfWeek(dayOfWeek);
        timetableCourse.setStartTime(LocalTime.parse(startTime));
        timetableCourse.setEndTime(LocalTime.parse(endTime));

        timetableCourseRepository.save(timetableCourse);
    }

    public void deleteClass(Long id) {
        timetableCourseRepository.deleteById(id);
    }
}
