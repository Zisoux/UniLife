package inhatc.hja.unilife.timetable.service;

import inhatc.hja.unilife.timetable.dto.CourseBlockDTO;
import inhatc.hja.unilife.timetable.entity.Course;
import inhatc.hja.unilife.timetable.entity.Timetable;
import inhatc.hja.unilife.timetable.entity.TimetableCourse;
import inhatc.hja.unilife.timetable.repository.CourseRepository;
import inhatc.hja.unilife.timetable.repository.TimetableCourseRepository;
import inhatc.hja.unilife.timetable.repository.TimetableRepository;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final TimetableCourseRepository timetableCourseRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    private static final double DAY_WIDTH_PERCENT = 19.8;

    public List<String> getAvailableSemesters() {
        List<String> result = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 5; year <= currentYear + 2; year++) {
            result.add(year + "-1학기");
            result.add(year + "-2학기");
        }
        return result;
    }

    public List<String> getSemestersByUserId(Long userId) {
        return timetableRepository.findAllByUserId(userId).stream()
                .map(Timetable::getSemester)
                .distinct()
                .sorted()
                .toList();
    }

    public Timetable getTimetableWithCourses(Long userId, String semester) {
        return timetableRepository.findByUserIdAndSemester(userId, semester)
                .orElseGet(() -> timetableRepository.save(createNewTimetable(userId, semester)));
    }

    public void createTimetable(Long userId, String semester) {
        boolean exists = !timetableRepository.findAllByUserIdAndSemester(userId, semester).isEmpty();
        if (!exists) {
            timetableRepository.save(createNewTimetable(userId, semester));
        }
    }

    public Timetable createNewTimetable(Long userId, String semester) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Timetable timetable = new Timetable();
        timetable.setUser(user);
        timetable.setSemester(semester);
        timetable.setCreatedAt(LocalDateTime.now());
        return timetable;
    }

    @Transactional
    public void addClassToTimetable(Long userId, Long courseId, String dayOfWeek, String startTime, String endTime, String semester) {
        Timetable timetable = getTimetableWithCourses(userId, semester);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID"));

        TimetableCourse timetableCourse = new TimetableCourse();
        timetableCourse.setCourse(course);
        timetableCourse.setDayOfWeek(dayOfWeek);
        timetableCourse.setStartTime(LocalTime.parse(startTime));
        timetableCourse.setEndTime(LocalTime.parse(endTime));

        timetable.addTimetableCourse(timetableCourse);

        timetableCourseRepository.save(timetableCourse);
    }

    @Transactional
    public void deleteClass(Long timetableCourseId) {
        TimetableCourse timetableCourse = timetableCourseRepository.findById(timetableCourseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid timetable course ID"));

        Timetable timetable = timetableCourse.getTimetable();
        timetable.removeTimetableCourse(timetableCourse);

        timetableCourseRepository.delete(timetableCourse);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<CourseBlockDTO> convertToCourseBlocks(List<TimetableCourse> courses) {
        List<CourseBlockDTO> blocks = new ArrayList<>();

        int timetableStart = 8 * 60;
        int timetableEnd = 22 * 60;
        int totalMinutes = timetableEnd - timetableStart;

        int pixelHeight = 1536;
        double pxPerMinute = 96.0 / 60.0; // 정확히 1시간 = 96px


        for (TimetableCourse course : courses) {
            LocalTime start = course.getStartTime();
            LocalTime end = course.getEndTime();

            int startTotal = start.getHour() * 60 + start.getMinute();
            int endTotal = end.getHour() * 60 + end.getMinute();

            int offset = Math.max(startTotal - timetableStart, 0);
            int topPx = (int) Math.round(offset * pxPerMinute) +32;


            int duration = endTotal - startTotal;
            int heightPx = (int) Math.round(duration * pxPerMinute);

            System.out.println("pxPerMinute: " + pxPerMinute + ", offset: " + offset + ", topPx: " + topPx);

            CourseBlockDTO block = new CourseBlockDTO(
                    course.getId(),
                    course.getCourse().getCourseName(),
                    course.getDayOfWeek(),
                    start.getHour(),
                    0.0,
                    0.0,
                    start.toString(),
                    end.toString(),
                    true
            );
            double left = calculateLeftPercent(course.getDayOfWeek());
            System.out.println("요일: " + course.getDayOfWeek() + ", leftPercent: " + left);
            block.setLeftPercent(left);
            
            block.setLeftPercent(calculateLeftPercent(course.getDayOfWeek()));
            block.setWidthPercent(18.0); //nono
            block.setTopPx(topPx);
            block.setHeightPx(heightPx);

            blocks.add(block);
        }

        return blocks;
    }

    public List<CourseBlockDTO> convertToCourseBlocksForFriend(List<TimetableCourse> courses) {
        List<CourseBlockDTO> blocks = new ArrayList<>();

        int timetableStart = 8 * 60;
        int timetableEnd = 22 * 60;
        double totalMinutes = (double) (timetableEnd - timetableStart);

        for (TimetableCourse course : courses) {
            LocalTime start = course.getStartTime();
            LocalTime end = course.getEndTime();

            int startTotal = start.getHour() * 60 + start.getMinute();
            int endTotal = end.getHour() * 60 + end.getMinute();

            double topPercent = ((startTotal - timetableStart) / totalMinutes) * 100.0;
            double heightPercent = ((endTotal - startTotal) / totalMinutes) * 100.0;

            CourseBlockDTO block = new CourseBlockDTO(
                    course.getId(),
                    course.getCourse().getCourseName(),
                    course.getDayOfWeek(),
                    0,
                    topPercent,
                    heightPercent,
                    start.toString(),
                    end.toString(),
                    false
            );
            block.setLeftPercent(calculateLeftPercent(course.getDayOfWeek()));
            block.setWidthPercent(DAY_WIDTH_PERCENT);
            blocks.add(block);
        }

        return blocks;
    }

    private double calculateLeftPercent(String dayOfWeek) {
        return switch (dayOfWeek) {
            case "월" -> 9.9;
            case "화" -> 27.9;
            case "수" -> 45.9;
            case "목" -> 63.9;
            case "금" -> 81.9;
            default -> 0.0;
        };

    }
    }
