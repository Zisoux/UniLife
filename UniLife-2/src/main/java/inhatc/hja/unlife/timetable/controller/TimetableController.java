package inhatc.hja.unlife.timetable.controller;

import inhatc.hja.unlife.timetable.repository.CourseRepository;
import inhatc.hja.unlife.timetable.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/timetable")
public class TimetableController {

    private final TimetableService timetableService;
    private final CourseRepository courseRepository;

    // 시간표 조회
    @GetMapping("/{userId}")
    public String viewTimetable(@PathVariable("userId") Long userId, Model model) {
        model.addAttribute("timetable", timetableService.getTimetableWithCourses(userId));
        model.addAttribute("courses", timetableService.getAllCourses());

        List<String> days = Arrays.asList("월", "화", "수", "목", "금");
        model.addAttribute("dayList", days);

        return "timetable";
    }

    // 수업 추가
    @PostMapping("/add")
    public String addClass(
            @RequestParam("userId") Long userId,
            @RequestParam("courseId") Long courseId,
            @RequestParam("dayOfWeek") String dayOfWeek,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime
    ) {
        timetableService.addClassToTimetable(userId, courseId, dayOfWeek, startTime, endTime);
        return "redirect:/timetable/" + userId;
    }

    // 수업 삭제
    @GetMapping("/delete/{id}")
    public String deleteClass(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        timetableService.deleteClass(id);
        return "redirect:/timetable/" + userId;
    }
}
