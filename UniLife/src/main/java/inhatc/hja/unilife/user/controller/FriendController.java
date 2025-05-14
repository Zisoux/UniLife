package inhatc.hja.unilife.user.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public List<SimpleUserDto> getFriendList(@AuthenticationPrincipal UserDetails userDetails) {
        // ✅ 로그인 ID(user_id)로 사용자 객체 조회
        String userLoginId = userDetails.getUsername(); // ex) 학번이 이 값

        // 👉 DB에서 실제 Long id(pk)를 가져오기
        User user = userRepository.findByUserId(userLoginId)
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return friendService.getFriendList(user.getId());
    }
}