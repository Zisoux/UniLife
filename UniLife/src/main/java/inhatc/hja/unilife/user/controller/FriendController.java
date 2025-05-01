package inhatc.hja.unilife.user.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import inhatc.hja.unilife.user.service.FriendService;
import inhatc.hja.unilife.user.dto.SimpleUserDto;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.UserRepository;

@RestController
public class FriendController {

    private final UserRepository userRepository;
    private final FriendService friendService;   // ← FriendService 주입

    public FriendController(UserRepository userRepository,
                            FriendService friendService) {
        this.userRepository = userRepository;
        this.friendService  = friendService;
    }

 // ✅ 친구 목록 API: userId = 1 하드코딩
    @GetMapping("/api/calendar/friends/list")
    public List<SimpleUserDto> getFriendList() {
        Long userId = 1L;  // 로그인 없이 하드코딩
        return friendService.getFriendList(userId);
    }

}