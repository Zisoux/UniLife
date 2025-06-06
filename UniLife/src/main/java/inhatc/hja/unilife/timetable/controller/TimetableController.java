package inhatc.hja.unilife.timetable.controller;

//import inhatc.hja.unilife.timetable.dto.CourseBlockDTO;
//import inhatc.hja.unilife.timetable.dto.FreeTimeMatchDTO;
import inhatc.hja.unilife.timetable.entity.Course;
import inhatc.hja.unilife.timetable.entity.Timetable;
//import inhatc.hja.unilife.user.repository.UserRepository;
import inhatc.hja.unilife.timetable.service.TimetableService;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
//import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<Timetable> optionalTimetable = timetableService.getTimetableWithCourses(userId, semester);
        if (optionalTimetable.isEmpty()) {
            // 시간표가 없는 경우 -> 빈 화면 표시 or 리다이렉트 or 메시지 출력
            model.addAttribute("user", userService.findById(userId));
            model.addAttribute("userId", userId);
            model.addAttribute("semester", semester);
            model.addAttribute("semesters", timetableService.getSemestersByUserId(userId));
            model.addAttribute("predefinedSemesters", timetableService.getAvailableSemesters());
            model.addAttribute("noTimetable", true); // view에서 분기처리용

            return "timetable/timetable"; // 뷰 파일은 동일하되, timetable 정보 없음 표시
        }
        Timetable timetable = optionalTimetable.get();

        List<Course> courses = timetableService.getAllCourses();
        List<String> semesters = timetableService.getSemestersByUserId(userId);
        List<String> predefinedSemesters = timetableService.getAvailableSemesters();

        String today = (dayOverride != null) ? dayOverride : getKoreanDayName(LocalDate.now().getDayOfWeek());
        // LocalTime now = (dayOverride != null) ? null : LocalTime.now();

        // List<FreeTimeMatchDTO> matchingFriends =
        // timetableService.findMatchingFriendsByDay(userId, semester, today, now);

        User user = userService.findById(userId);

        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        model.addAttribute("semester", semester);
        model.addAttribute("semesters", semesters);
        model.addAttribute("predefinedSemesters", predefinedSemesters);
        model.addAttribute("timetable", timetable);
        model.addAttribute("courses", courses);
        model.addAttribute("dayList", List.of("월", "화", "수", "목", "금"));
        model.addAttribute("selectedDay", today);
        // model.addAttribute("matchingFriends", matchingFriends);
        model.addAttribute("courseBlocks", timetableService.convertToCourseBlocks(timetable.getTimetableCourses()));

        return "timetable/timetable";
    }

    @PostMapping("/create")
    public String createTimetable(@RequestParam("userId") Long userId,
            @RequestParam("semester") String semester) {
        timetableService.createTimetable(userId, semester);
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