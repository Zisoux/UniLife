package inhatc.hja.unilife.timetable.service;

import inhatc.hja.unilife.timetable.dto.*;
import inhatc.hja.unilife.timetable.entity.*;
import inhatc.hja.unilife.timetable.repository.*;
import inhatc.hja.unilife.user.repository.entity.User;
import jakarta.transaction.Transactional;
import inhatc.hja.unilife.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final TimetableCourseRepository timetableCourseRepository;
    private final CourseRepository courseRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

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

    //2025-05-21 수정 (내가 추가한 학기 시간표가 친구한테도 생기는 문제점)
    public Timetable getTimetableWithCourses(Long userId, String semester) {
        return timetableRepository.findByUserIdAndSemester(userId, semester)
                .orElseGet(() -> {
                    Timetable newTimetable = createNewTimetable(userId, semester);
                    return timetableRepository.save(newTimetable);
                });
    }

    public Timetable getExistingTimetableWithCourses(Long userId, String semester) {
        return timetableRepository.findByUserIdAndSemester(userId, semester)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteTimetable(Long userId, String semester) {
        Timetable timetable = timetableRepository.findByUserIdAndSemester(userId, semester)
            .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));
        timetableRepository.delete(timetable);
    }

    


    private Timetable createNewTimetable(Long userId, String semester) {
        Timetable timetable = new Timetable();
        timetable.setSemester(semester);
        timetable.setCreatedAt(LocalDateTime.now());

        // 🔥 user 연결 추가
        userRepository.findById(userId).ifPresent(timetable::setUser);

        return timetable;
    }


    public void createTimetable(Long userId, String semester) {
        boolean exists = timetableRepository.findByUserIdAndSemester(userId, semester).isPresent();
        if (!exists) {
            Timetable timetable = createNewTimetable(userId, semester);
            timetableRepository.save(timetable);
        }
    }


    public void addClassToTimetable(Long userId, Long courseId, String dayOfWeek, String startTime, String endTime, String semester) {
        Timetable timetable = getTimetableWithCourses(userId, semester);
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

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<FreeTimeMatchDTO> findMatchingFriendsByDay(Long userId, String semester, String dayOfWeek, LocalTime now) {
        List<TimetableCourse> myCourses = timetableCourseRepository.findByTimetableUserIdAndTimetableSemester(userId, semester)
                .stream().filter(c -> c.getDayOfWeek().equals(dayOfWeek)).toList();
        List<TimeRange> myFreeTimes = calculateFreeTimes(myCourses);

        List<Friend> friends = friendRepository.findByUserIdAndStatus(userId, Friend.Status.accepted);
        Map<Long, FreeTimeMatchDTO> matchMap = new HashMap<>();

        for (Friend friend : friends) {
            // 🔒 학기 시간표가 없는 친구는 건너뜀
            boolean hasSemester = timetableRepository.findByUserIdAndSemester(friend.getFriendId(), semester).isPresent();
            if (!hasSemester) continue;

            List<TimetableCourse> friendCourses = timetableCourseRepository.findByTimetableUserIdAndTimetableSemester(friend.getFriendId(), semester)
                    .stream().filter(c -> c.getDayOfWeek().equals(dayOfWeek)).toList();
            List<TimeRange> friendFreeTimes = calculateFreeTimes(friendCourses);

            for (TimeRange my : myFreeTimes) {
                for (TimeRange fr : friendFreeTimes) {
                    Optional<TimeRange> overlap = getOverlap(my, fr);
                    if (overlap.isPresent() && (now == null || isCurrentTimeInRange(overlap.get(), now))) {
                        TimeRange range = overlap.get();
                        String slot = range.start() + " ~ " + range.end();

                        userRepository.findById(friend.getFriendId()).ifPresent(user -> {
                            matchMap.computeIfAbsent(user.getId(), k -> {
                                FreeTimeMatchDTO dto = new FreeTimeMatchDTO();
                                dto.setFriendId(user.getId());
                                dto.setFriendUsername(user.getUsername());
                                dto.setDayOfWeek(dayOfWeek);
                                dto.setTimeRanges(new ArrayList<>());
                                return dto;
                            }).getTimeRanges().add(slot);
                        });
                    }
                }
            }
        }

        return new ArrayList<>(matchMap.values());
    }


    private List<TimeRange> calculateFreeTimes(List<TimetableCourse> courses) {
        List<TimetableCourse> sortedCourses = new ArrayList<>(courses);
        sortedCourses.sort(Comparator.comparing(TimetableCourse::getStartTime));

        List<TimeRange> freeTimes = new ArrayList<>();
        LocalTime dayStart = LocalTime.of(8, 0);  // 🔥 9시 → 8시
        LocalTime dayEnd = LocalTime.of(22, 0);   // 🔥 18시 → 22시

        for (TimetableCourse course : sortedCourses) {
            if (course.getStartTime().isAfter(dayStart)) {
                freeTimes.add(new TimeRange(dayStart, course.getStartTime()));
            }
            dayStart = course.getEndTime().isAfter(dayStart) ? course.getEndTime() : dayStart;
        }

        if (dayStart.isBefore(dayEnd)) {
            freeTimes.add(new TimeRange(dayStart, dayEnd));
        }

        return freeTimes;
    }

    private Optional<TimeRange> getOverlap(TimeRange t1, TimeRange t2) {
        LocalTime start = t1.start().isAfter(t2.start()) ? t1.start() : t2.start();
        LocalTime end = t1.end().isBefore(t2.end()) ? t1.end() : t2.end();
        return start.isBefore(end) ? Optional.of(new TimeRange(start, end)) : Optional.empty();
    }

    private boolean isCurrentTimeInRange(TimeRange range, LocalTime now) {
        return now.isAfter(range.start()) && now.isBefore(range.end());
    }

    public List<CourseBlockDTO> convertToCourseBlocks(List<TimetableCourse> courses) {
        List<CourseBlockDTO> blocks = new ArrayList<>();

        int timetableStart = 8 * 60;        // 08:00 시작 (분)
        int timetableEnd = 22 * 60;         // 22:00 종료
        int timetableDuration = timetableEnd - timetableStart; // 840분
        int pixelHeight = 1440;             // 전체 높이(px)
        double pxPerMinute = pixelHeight / 840.0;
        
        for (TimetableCourse course : courses) {
            LocalTime start = course.getStartTime();
            LocalTime end = course.getEndTime();

            int startTotal = start.getHour() * 60 + start.getMinute();
            int endTotal = end.getHour() * 60 + end.getMinute();
            int duration = endTotal - startTotal;

            int topPx = (int) ((startTotal - timetableStart) * pxPerMinute) +17;  // ✅ 정확히 1시간 보정

            int heightPx = (int) (duration * pxPerMinute);

            CourseBlockDTO block = new CourseBlockDTO(
                course.getId(),
                course.getCourse().getCourseName(),
                course.getDayOfWeek(),
                start.getHour(),
                0.0,  // topPercent (사용 안함)
                0.0,  // heightPercent (사용 안함)
                start.toString(),
                end.toString(),
                true
            );

            block.setLeftPercent(calculateLeftPercent(course.getDayOfWeek()));
            block.setWidthPercent(18.0);
            block.setTopPx(topPx);
            block.setHeightPx(heightPx);

            // 디버그 로그
            System.out.printf("📦 [px] %s %s (%s ~ %s) → top=%dpx, height=%dpx\n",
                block.getDayOfWeek(), block.getCourseName(), block.getStartTime(), block.getEndTime(), topPx, heightPx);

            blocks.add(block);
        }

        return blocks;
    }




 // ✅ 기존 함수 전체 교체
    private double calculateLeftPercent(String dayOfWeek) {
        switch (dayOfWeek) {
            case "월": return 10.0;
            case "화": return 28.0;
            case "수": return 46.0;
            case "목": return 64.0;
            case "금": return 82.0;
            default: return 10.0;
        }
    }




    public List<CourseBlockDTO> convertToCourseBlocksForFriend(List<TimetableCourse> courses) {
        List<CourseBlockDTO> blocks = new ArrayList<>();

        for (TimetableCourse course : courses) {
            LocalTime start = course.getStartTime();
            LocalTime end = course.getEndTime();

            int startTotal = start.getHour() * 60 + start.getMinute();
            int endTotal = end.getHour() * 60 + end.getMinute();

            double totalMinutes = (22 - 7) * 60.0; // 900분
            double topPercent = ((startTotal - 7 * 60) / totalMinutes) * 100.0;
            double heightPercent = ((endTotal - startTotal) / totalMinutes) * 100.0;

            CourseBlockDTO block = new CourseBlockDTO(
                course.getId(),
                course.getCourse().getCourseName(),
                course.getDayOfWeek(),
                0, // hour는 friend 화면에서 필요 없음
                topPercent,
                heightPercent,
                start.toString(),
                end.toString(),
                false
            );

            block.setLeftPercent(calculateLeftPercent(course.getDayOfWeek()));
            block.setWidthPercent(20.0);

            blocks.add(block);
        }

        return blocks;
    }







           
    public List<FreeTimeMatchDTO> findMatchingFriends(Long userId, String semester, String dayOfWeek, LocalTime startTime, LocalTime endTime, int minMinutes) {
        List<TimetableCourse> myCourses = timetableCourseRepository.findByTimetableUserIdAndTimetableSemester(userId, semester)
                .stream()
                .filter(c -> c.getDayOfWeek().equals(dayOfWeek))
                .toList();
        List<TimeRange> myFreeTimes = calculateFreeTimes(myCourses);

        List<Friend> friends = friendRepository.findByUserIdAndStatus(userId, Friend.Status.accepted);
        Map<Long, FreeTimeMatchDTO> friendMatchMap = new HashMap<>();

        for (Friend friend : friends) {
            // ✅ 시간표 존재 여부 먼저 확인
            boolean hasSemester = timetableRepository.findByUserIdAndSemester(friend.getFriendId(), semester).isPresent();
            if (!hasSemester) continue;

            List<TimetableCourse> friendCourses = timetableCourseRepository.findByTimetableUserIdAndTimetableSemester(friend.getFriendId(), semester)
                    .stream()
                    .filter(c -> c.getDayOfWeek().equals(dayOfWeek))
                    .toList();
            List<TimeRange> friendFreeTimes = calculateFreeTimes(friendCourses);

            for (TimeRange my : myFreeTimes) {
                for (TimeRange fr : friendFreeTimes) {
                    Optional<TimeRange> overlap = getOverlap(my, fr);
                    if (overlap.isPresent()) {
                        TimeRange overlapRange = overlap.get();

                        if (overlapRange.start().isBefore(endTime) && overlapRange.end().isAfter(startTime)) {
                            LocalTime matchedStart = overlapRange.start().isAfter(startTime) ? overlapRange.start() : startTime;
                            LocalTime matchedEnd = overlapRange.end().isBefore(endTime) ? overlapRange.end() : endTime;

                            int matchedMinutes = (int) Duration.between(matchedStart, matchedEnd).toMinutes();

                            if (matchedMinutes >= minMinutes) {
                                userRepository.findById(friend.getFriendId()).ifPresent(user -> {
                                    FreeTimeMatchDTO dto = friendMatchMap.getOrDefault(user.getId(), new FreeTimeMatchDTO());
                                    dto.setFriendId(user.getId());
                                    dto.setFriendUsername(user.getUsername());
                                    dto.setDayOfWeek(dayOfWeek);

                                    if (dto.getTimeRanges() == null) {
                                        dto.setTimeRanges(new ArrayList<>());
                                    }

                                    dto.getTimeRanges().add(matchedStart + " ~ " + matchedEnd);
                                    friendMatchMap.put(user.getId(), dto);
                                });
                            }
                        }
                    }
                }
            }
        }

        return new ArrayList<>(friendMatchMap.values());
    }
}

