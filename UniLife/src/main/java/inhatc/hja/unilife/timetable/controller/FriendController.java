package inhatc.hja.unilife.timetable.controller;

import inhatc.hja.unilife.timetable.dto.CourseBlockDTO;
import inhatc.hja.unilife.timetable.dto.FriendWithUser;
import inhatc.hja.unilife.timetable.entity.Timetable;
import inhatc.hja.unilife.timetable.service.FriendService;
import inhatc.hja.unilife.timetable.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final TimetableService timetableService;

    // 친구 목록 보기
    @GetMapping("/{userId}")
    public String viewFriends(@PathVariable("userId") Long userId,
                               @RequestParam(name = "semester", required = false) String semester,
                               Model model) {
        if (semester == null || semester.isBlank()) {
            semester = "2024-1학기"; // 기본 학기 지정
        }

        model.addAttribute("userId", userId);
        model.addAttribute("semester", semester);
        model.addAttribute("friends", friendService.getFriends(userId));
        return "timetable/friends";
    }

    // 친구 검색
    @PostMapping("/search")
    public String searchFriends(@RequestParam("userId") Long userId,
                                 @RequestParam("semester") String semester,
                                 @RequestParam("keyword") String keyword,
                                 Model model) {
        List<FriendWithUser> searchResults = friendService.searchFriends(userId, keyword);
        model.addAttribute("userId", userId);
        model.addAttribute("semester", semester);
        model.addAttribute("friends", searchResults);
        return "timetable/friends";
    }

    // 친구 추가
    @PostMapping("/add")
    public String addFriend(@RequestParam("userId") Long userId,
                             @RequestParam("friendId") Long friendId) {
        friendService.addFriend(userId, friendId);
        return "redirect:/friends/" + userId;
    }

    // 친구 삭제
    @PostMapping("/delete")
    public String deleteFriend(@RequestParam("userId") Long userId,
                                @RequestParam("friendId") Long friendId) {
        friendService.deleteFriend(userId, friendId);
        return "redirect:/friends/" + userId;
    }

    // 친구 시간표 보기
    @GetMapping("/{userId}/timetable/{friendId}")
    public String viewFriendTimetable(@PathVariable("userId") Long userId,
                                      @PathVariable("friendId") Long friendId,
                                      @RequestParam("semester") String semester,
                                      Model model) {

        Timetable friendTimetable;
        List<CourseBlockDTO> courseBlocks;

        try {
            friendTimetable = timetableService.getTimetableWithCourses(friendId, semester);
            courseBlocks = timetableService.convertToCourseBlocksForFriend(friendTimetable.getTimetableCourses());
        } catch (IllegalArgumentException e) {
            // 친구가 해당 학기에 시간표가 없는 경우 예외 발생 → 빈 시간표 처리
            friendTimetable = new Timetable();
            friendTimetable.setTimetableCourses(new ArrayList<>());
            courseBlocks = new ArrayList<>();
        }

        List<String> semesters = timetableService.getSemestersByUserId(friendId);

        model.addAttribute("userId", userId);
        model.addAttribute("friendId", friendId);
        model.addAttribute("semester", semester);
        model.addAttribute("semesters", semesters);
        model.addAttribute("timetable", friendTimetable);
        model.addAttribute("dayList", List.of("월", "화", "수", "목", "금"));
        model.addAttribute("courseBlocks", courseBlocks);

        return "timetable/friendTimetable";  // ✅ 수정됨
    }
}
