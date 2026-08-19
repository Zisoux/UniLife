package inhatc.hja.unilife.user.controller;

import inhatc.hja.unilife.timetable.dto.CourseBlockDTO;
import inhatc.hja.unilife.user.dto.FriendWithUser;
import inhatc.hja.unilife.timetable.entity.Timetable;
import inhatc.hja.unilife.timetable.entity.TimetableCourse;
import inhatc.hja.unilife.user.service.FriendService;
import inhatc.hja.unilife.timetable.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final TimetableService timetableService;

    // 친구 목록 보기
    @GetMapping("/{userId}")
    public String viewFriends(@PathVariable("userId") String userId,
                               @RequestParam(name = "semester", required = false) String semester,
                               @RequestParam(name = "error", required = false) String error,
                               Model model) {
        if (semester == null || semester.isBlank()) {
            semester = "2024-1학기";
        }

        model.addAttribute("userId", userId);
        model.addAttribute("semester", semester);
        model.addAttribute("friends", friendService.getFriendsByUserId(userId));
        model.addAttribute("error", error);
        return "timetable/friends";
    }

    // 친구 검색
    @PostMapping("/search")
    public String searchFriends(@RequestParam("userId") String userId,
                                 @RequestParam("semester") String semester,
                                 @RequestParam("keyword") String keyword,
                                 Model model) {
        List<FriendWithUser> searchResults = friendService.searchFriendsByUserId(userId, keyword);
        model.addAttribute("userId", userId);
        model.addAttribute("semester", semester);
        model.addAttribute("friends", searchResults);
        return "timetable/friends";
    }

    // 친구 추가
    @PostMapping("/add")
    public String addFriend(@RequestParam("userId") String userId,
                            @RequestParam("friendUserId") String friendUserId,
                            @RequestParam("semester") String semester) {

        String encodedSemester = URLEncoder.encode(semester, StandardCharsets.UTF_8);

        if (friendUserId == null || friendUserId.isBlank()) {
            return "redirect:/friends/" + userId + "?semester=" + encodedSemester + "&error=emptyFriendId";
        }

        if (userId.equals(friendUserId)) {
            return "redirect:/friends/" + userId + "?semester=" + encodedSemester + "&error=selfAdd";
        }

        boolean alreadyFriend = friendService.isAlreadyFriendByUserId(userId, friendUserId);
        if (alreadyFriend) {
            return "redirect:/friends/" + userId + "?semester=" + encodedSemester + "&error=alreadyFriend";
        }

        try {
            friendService.addFriendByUserId(userId, friendUserId);
        } catch (IllegalArgumentException e) {
            return "redirect:/friends/" + userId + "?semester=" + encodedSemester + "&error=invalidFriend";
        }

        return "redirect:/friends/" + userId + "?semester=" + encodedSemester;
    }

    // 친구 삭제
    @PostMapping("/delete")
    public String deleteFriend(@RequestParam("userId") String userId,
                               @RequestParam("friendUserId") String friendUserId,
                               @RequestParam("semester") String semester) {

        String encodedSemester = URLEncoder.encode(semester, StandardCharsets.UTF_8);
        friendService.deleteFriend(userId, friendUserId);
        return "redirect:/friends/" + userId + "?semester=" + encodedSemester;
    }

    // 친구 시간표 보기
    @GetMapping("/{userId}/timetable/{friendId}")
    public String viewFriendTimetable(@PathVariable("userId") String userId,
                                      @PathVariable("friendId") Long friendId,
                                      @RequestParam("semester") String semester,
                                      @RequestParam(value = "error", required = false) String error,
                                      Model model) {

        Timetable friendTimetable = timetableService.getTimetableWithCourses(friendId, semester);
        List<TimetableCourse> friendCourses = friendTimetable.getTimetableCourses();

        if (friendCourses == null || friendCourses.isEmpty()) {
            String encodedSemester = URLEncoder.encode(semester, StandardCharsets.UTF_8);
            return "redirect:/friends/" + userId + "?semester=" + encodedSemester + "&error=notfound";
        }

        List<CourseBlockDTO> courseBlocks = timetableService.convertToCourseBlocksForFriend(friendCourses);
        List<String> semesters = timetableService.getSemestersByUserId(friendId);

        model.addAttribute("userId", userId);
        model.addAttribute("friendId", friendId);
        model.addAttribute("semester", semester);
        model.addAttribute("semesters", semesters);
        model.addAttribute("timetable", friendTimetable);
        model.addAttribute("dayList", List.of("월", "화", "수", "목", "금"));
        model.addAttribute("courseBlocks", courseBlocks);

        return "timetable/friendTimetable";
    }
}
