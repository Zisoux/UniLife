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
    public void addClassToTimetable(Long userId, Long courseId, String customCourseName,
                                    String dayOfWeek, String startTime, String endTime, String semester) {

        LocalTime start = LocalTime.parse(startTime);
        if (start.isBefore(LocalTime.of(8, 0))) {
            throw new IllegalArgumentException("⚠ 08:00 이전 강의는 등록할 수 없습니다.");
        }

        Timetable timetable = getTimetableWithCourses(userId, semester);
        TimetableCourse timetableCourse = new TimetableCourse();

        timetableCourse.setTimetable(timetable);
        timetableCourse.setDayOfWeek(dayOfWeek);
        timetableCourse.setStartTime(start);
        timetableCourse.setEndTime(LocalTime.parse(endTime));

        if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 강의입니다."));
            timetableCourse.setCourse(course); // ✅ null 넣지 말고 실제 course 설정
            timetableCourse.setCustomTitle(customCourseName); // customName은 있을 수도 있고 없을 수도 있음
        } else if (customCourseName != null && !customCourseName.trim().isEmpty()) {
            Course dummy = courseRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("직접 입력 강의 사용 시 더미 Course가 필요합니다."));
            timetableCourse.setCourse(dummy); // ✅ 더미 course로 설정
            timetableCourse.setCustomTitle(customCourseName);
        } else {
            throw new IllegalArgumentException("강의를 선택하거나 직접 입력해주세요.");
        }

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

        int timetableStart = 8 * 60; // 08:00
        int timetableEnd = 22 * 60;  // 22:00
        int totalMinutes = timetableEnd - timetableStart;

        double pxPerMinute = 64.0 / 60.0;  // 1분당 px
        int headerOffset = 0;

        for (TimetableCourse course : courses) {
            LocalTime start = course.getStartTime();
            LocalTime end = course.getEndTime();

            int startTotal = start.getHour() * 60 + start.getMinute();
            int endTotal = end.getHour() * 60 + end.getMinute();

            if (startTotal < timetableStart || endTotal > timetableEnd) {
                System.out.println("강의 시간이 시간표 범위를 벗어남: " + course.getCourse().getCourseName());
                continue;
            }

            int offsetMinutes = startTotal - timetableStart;
            int topPx = (int) Math.round(offsetMinutes * pxPerMinute);
            int durationMinutes = endTotal - startTotal;
            int heightPx = (int) Math.round(durationMinutes * pxPerMinute);
            heightPx = Math.max(heightPx, 30);

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

            block.setTopPx(topPx);
            block.setHeightPx(heightPx);
            block.setLeftPercent(calculateLeftPercent(course.getDayOfWeek()));
            block.setWidthPercent(17.5);
            block.setCustomTitle(course.getCustomTitle()); // ✅ 핵심 수정

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

            if (startTotal < timetableStart || endTotal > timetableEnd) {
                continue; // 범위 외 강의 무시
            }

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
        // 10% 시간 컬럼 + 각 요일별 18% 컬럼
        return switch (dayOfWeek) {
            case "월" -> 10;  // 약간의 여백 추가
            case "화" -> 28;  
            case "수" -> 46;  
            case "목" -> 64;  
            case "금" -> 82;  
            default -> 10;
        };
    }
    }
