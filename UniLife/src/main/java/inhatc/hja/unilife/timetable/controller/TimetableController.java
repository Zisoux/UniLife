package inhatc.hja.unilife.timetable.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import inhatc.hja.unilife.timetable.dto.CourseBlockDTO;
import inhatc.hja.unilife.timetable.entity.Course;
import inhatc.hja.unilife.timetable.entity.Timetable;
import inhatc.hja.unilife.timetable.entity.TimetableCourse;
import inhatc.hja.unilife.timetable.service.TimetableService;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/timetable")
public class TimetableController {

    private final UserService userService;

    private final TimetableService timetableService;
    // private final UserRepository userRepository;

@GetMapping("/view/{userId}")
public String viewTimetable(@PathVariable("userId") Long userId,
                            @RequestParam("semester") String semester,
                            @RequestParam(value = "day", required = false) String dayOverride,
                            Model model) {

    Timetable timetable = timetableService.getTimetableWithCourses(userId, semester); // ✅ 단일 호출
    List<TimetableCourse> timetableCourses = timetable.getTimetableCourses();         // ✅ 바로 꺼내서 사용

    User user = userService.findById(userId);
    List<Course> courses = timetableService.getAllCourses();
    List<String> semesters = timetableService.getSemestersByUserId(userId);
    List<String> predefinedSemesters = timetableService.getAvailableSemesters();
    String today = (dayOverride != null) ? dayOverride : getKoreanDayName(LocalDate.now().getDayOfWeek());
    List<CourseBlockDTO> blocks = timetableService.convertToCourseBlocks(timetableCourses);

    model.addAttribute("user", user);
    model.addAttribute("userId", userId);
    model.addAttribute("semester", semester);
    model.addAttribute("semesters", semesters);
    model.addAttribute("predefinedSemesters", predefinedSemesters);
    model.addAttribute("dayList", List.of("월", "화", "수", "목", "금"));
    model.addAttribute("selectedDay", today);
    model.addAttribute("timetable", timetable);
    model.addAttribute("courses", courses);
    model.addAttribute("courseBlocks", blocks); // 이름을 courseBlocks로 맞추기
    // model.addAttribute("matchingFriends", Collections.emptyList());

    return "timetable/timetable";
}


@PostMapping("/create")
public String createTimetable(@RequestParam("userId") Long userId,
                              @RequestParam("semester") String semester) {
    if (semester == null || semester.trim().isEmpty()) {
        return "redirect:/error"; // 혹은 사용자 친화적 메시지
    }

    timetableService.createTimetable(userId, semester);

    // URL 인코딩 (한글 등 처리)
    String encoded = URLEncoder.encode(semester, StandardCharsets.UTF_8);
    return "redirect:/timetable/view/" + userId + "?semester=" + encoded;
}


    @PostMapping("/add")
    public String addClass(@RequestParam("userId") Long userId,
            @RequestParam("courseId") Long courseId,
            @RequestParam("dayOfWeek") String dayOfWeek,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            @RequestParam("semester") String semester) {
        timetableService.addClassToTimetable(userId, courseId, dayOfWeek, startTime, endTime, semester);
        String encodedSemester = URLEncoder.encode(semester, StandardCharsets.UTF_8);
        return "redirect:/timetable/view/" + userId + "?semester=" + encodedSemester;
    }

    @GetMapping("/delete/{id}")
    public String deleteClass(@PathVariable("id") Long id,
            @RequestParam("userId") Long userId,
            @RequestParam("semester") String semester) {
        timetableService.deleteClass(id);
        String encodedSemester = URLEncoder.encode(semester, StandardCharsets.UTF_8);
        return "redirect:/timetable/view/" + userId + "?semester=" + encodedSemester;
    }

    @GetMapping("/view/{userId}/match-free")
    public String matchFreeTimeFriends(@PathVariable("userId") Long userId,
            @RequestParam("semester") String semester,
            @RequestParam("dayOfWeek") String dayOfWeek,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            @RequestParam(value = "minMinutes", defaultValue = "30") int minMinutes,
            Model model) {

        /*
         * List<FreeTimeMatchDTO> matches = timetableService.findMatchingFriends(
         * userId, semester, dayOfWeek, LocalTime.parse(startTime),
         * LocalTime.parse(endTime), minMinutes
         * );
         */

        List<Integer> timeLinePositions = new ArrayList<>();
        for (int hour = 9; hour <= 18; hour++) {
            timeLinePositions.add((int) ((hour - 9) * (100.0 / 9)));
        }

        // model.addAttribute("matches", matches);
        model.addAttribute("userId", userId);
        model.addAttribute("semester", semester);
        model.addAttribute("dayOfWeek", dayOfWeek);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("minMinutes", minMinutes);
        model.addAttribute("dayList", List.of("월", "화", "수", "목", "금"));
        model.addAttribute("timeLinePositions", timeLinePositions);

        return "freeTimeMatches";
    }

    private String getKoreanDayName(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            default -> "월";
        };
    }
}