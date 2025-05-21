package inhatc.hja.unilife.timetable.controller;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import inhatc.hja.unilife.timetable.dto.CourseBlockDTO;
import inhatc.hja.unilife.timetable.dto.FreeTimeMatchDTO;
import inhatc.hja.unilife.timetable.entity.Course;
import inhatc.hja.unilife.timetable.entity.Timetable;
import inhatc.hja.unilife.user.repository.UserRepository;
import inhatc.hja.unilife.timetable.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/timetable")
public class TimetableController {

    private final TimetableService timetableService;
    private final UserRepository userRepository;

    @GetMapping("/view/{userId}")
    public String viewTimetable(@PathVariable("userId") Long userId,
                                @RequestParam(value = "semester", required = false) String semester,
                                @RequestParam(value = "day", required = false) String dayOverride,
                                Model model) {

        List<String> semesters = timetableService.getSemestersByUserId(userId);
        List<String> predefinedSemesters = timetableService.getAvailableSemesters();

        // 요청에 semester 없으면 가장 최근 학기로 설정
        if (semester == null || semester.isEmpty()) {
            semester = semesters.isEmpty() ? "" : semesters.get(0);
        }

        Timetable timetable = semester.isEmpty() ? null : timetableService.getTimetableWithCourses(userId, semester);
        List<Course> courses = timetableService.getAllCourses();

        String today = (dayOverride != null) ? dayOverride : getKoreanDayName(LocalDate.now().getDayOfWeek());
        LocalTime now = (dayOverride != null) ? null : LocalTime.now();
        List<FreeTimeMatchDTO> matchingFriends = (timetable != null)
            ? timetableService.findMatchingFriendsByDay(userId, semester, today, now)
            : new ArrayList<>();

        model.addAttribute("userId", userId);
        model.addAttribute("semester", semester);
        model.addAttribute("semesters", semesters);
        model.addAttribute("predefinedSemesters", predefinedSemesters);
        model.addAttribute("timetable", timetable);
        model.addAttribute("courses", courses);
        model.addAttribute("dayList", List.of("월", "화", "수", "목", "금"));
        model.addAttribute("selectedDay", today);
        model.addAttribute("matchingFriends", matchingFriends);
        model.addAttribute("courseBlocks", (timetable != null)
            ? timetableService.convertToCourseBlocks(timetable.getTimetableCourses())
            : new ArrayList<>());

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
  

    @PostMapping("/delete")
    public String deleteTimetable(@RequestParam("userId") Long userId,
                                  @RequestParam("semester") String semester) {
        timetableService.deleteTimetable(userId, semester);

        // 남아있는 학기 중 첫 번째로 리디렉션
        List<String> remainingSemesters = timetableService.getSemestersByUserId(userId);
        if (!remainingSemesters.isEmpty()) {
            String nextSemester = remainingSemesters.get(0);
            String encodedNext = URLEncoder.encode(nextSemester, StandardCharsets.UTF_8);
            return "redirect:/timetable/view/" + userId + "?semester=" + encodedNext;
        }

        // 남은 학기가 없으면 빈 시간표 화면으로 이동
        return "redirect:/timetable/view/" + userId + "?semester=";
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

        List<FreeTimeMatchDTO> matches = timetableService.findMatchingFriends(
            userId, semester, dayOfWeek, LocalTime.parse(startTime), LocalTime.parse(endTime), minMinutes
        );

        List<Integer> timeLinePositions = new ArrayList<>();
        for (int hour = 9; hour <= 18; hour++) {
            timeLinePositions.add((int)((hour - 9) * (100.0 / 9)));
        }

        model.addAttribute("matches", matches);
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
