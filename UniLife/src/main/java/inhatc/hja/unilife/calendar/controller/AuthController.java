package inhatc.hja.unilife.calendar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import inhatc.hja.unilife.user.dto.UserDto;
import inhatc.hja.unilife.user.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "common/login";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "common/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute UserDto userDto, Model model, HttpSession session) {
        String verifiedEmail = (String) session.getAttribute("emailVerified");

        // 이메일 인증 체크
        if (verifiedEmail == null || !verifiedEmail.equals(userDto.getEmail())) {
            model.addAttribute("emailError", "이메일 인증을 완료해주세요.");
            return "common/signup";
        }

        // 비밀번호 일치 여부 확인
        if (!userDto.getPassword().equals(userDto.getPasswordck())) {
            model.addAttribute("pwError", "비밀번호를 확인하세요.");
            return "redirect:/signup?pwError";
        }

        boolean success = userService.register(userDto);
        if (success) {
            session.removeAttribute("emailVerified"); // 인증 정보 삭제
            return "redirect:/login?signupSuccess";
        } else {
            model.addAttribute("idError", "이미 존재하는 아이디입니다.");
            return "common/signup";
        }
    }

}