package inhatc.hja.unilife.calendar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import inhatc.hja.unilife.user.dto.SimpleUserDto;
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
    public String signup(@ModelAttribute SimpleUserDto userDto, Model model, HttpSession session) {
        String verifiedEmail = (String) session.getAttribute("emailVerified");

        if (verifiedEmail == null || !verifiedEmail.equals(userDto.getEmail())) {
            model.addAttribute("error", "이메일 인증을 완료해주세요.");
            return "common/signup";
        }

        boolean success = userService.register(userDto);
        if (success) {
            session.removeAttribute("emailVerified"); // 인증 정보 삭제
            return "redirect:/login?signupSuccess";
        } else {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            return "common/signup";
        }
    }

}