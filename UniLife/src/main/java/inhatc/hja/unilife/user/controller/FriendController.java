package inhatc.hja.unilife.user.controller;

import inhatc.hja.unilife.timetable.dto.CourseBlockDTO;
import inhatc.hja.unilife.user.dto.FriendWithUser;
import inhatc.hja.unilife.timetable.entity.Timetable;
import inhatc.hja.unilife.user.service.FriendService;
import inhatc.hja.unilife.timetable.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
            @RequestParam("friendId") String friendIdStr) {

        // 1. 비어있거나 숫자가 아닌 경우
        if (friendIdStr == null || friendIdStr.isBlank()) {
            return "redirect:/friends/" + userId + "?error=emptyFriendId";
        }

        Long friendId;
        try {
            friendId = Long.parseLong(friendIdStr);
        } catch (NumberFormatException e) {
            return "redirect:/friends/" + userId + "?error=invalidFriend";
        }

        // 2. 자기 자신 추가 방지
        if (userId.equals(friendId)) {
            return "redirect:/friends/" + userId + "?error=selfAdd";
        }

        // 3. 이미 친구인지 확인
        boolean alreadyFriend = friendService.isAlreadyFriend(userId, friendId);
        if (alreadyFriend) {
            return "redirect:/friends/" + userId + "?error=alreadyFriend";
        }

        // 4. 친구 추가 시도
        try {
            friendService.addFriend(userId, friendId);
        } catch (IllegalArgumentException e) {
            return "redirect:/friends/" + userId + "?error=invalidFriend";
        }

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
            @RequestParam(value = "error", required = false) String error,
            Model model) {

        Optional<Timetable> optionalFriendTimetable = timetableService.getTimetableWithCourses(friendId, semester);

        if (optionalFriendTimetable.isEmpty()) {
            return "redirect:/friends/" + userId + "?error=notfound";
        }

        Timetable friendTimetable = optionalFriendTimetable.get();
        List<CourseBlockDTO> courseBlocks = timetableService
                .convertToCourseBlocksForFriend(friendTimetable.getTimetableCourses());

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