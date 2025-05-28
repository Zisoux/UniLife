package inhatc.hja.unilife.user.controller;

import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** 내 정보 조회 */
    @GetMapping
    public String showMyPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String loginUserId = userDetails.getUsername(); // 학번
        User user = userRepository.findByUserId(loginUserId)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        model.addAttribute("user", user);
        return "common/mypage"; // -> mypage.html
    }

    /** 내 정보 수정 */
    @PostMapping("/update")
    public String updateMyInfo(@AuthenticationPrincipal UserDetails userDetails,
                               @ModelAttribute("user") User formUser,
                               @RequestParam(value = "newPassword", required = false) String newPassword) {

        String loginUserId = userDetails.getUsername();
        User user = userRepository.findByUserId(loginUserId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 정보 업데이트
        user.setUsername(formUser.getUsername());
        user.setEmail(formUser.getEmail());
        user.setDepartment(formUser.getDepartment());
        user.setNotificationEnabled(formUser.getNotificationEnabled());

        // 비밀번호 변경 요청이 있는 경우
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            String encoded = passwordEncoder.encode(newPassword);
            user.setPasswordHash(encoded);
        }

        userRepository.save(user);
        return "redirect:/mypage?success";
    }

    /** 회원 탈퇴 */
    @GetMapping("/delete")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        String loginUserId = userDetails.getUsername();
        User user = userRepository.findByUserId(loginUserId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        userRepository.delete(user);
        session.invalidate(); // 세션 종료
        return "redirect:/logout";
    }
}