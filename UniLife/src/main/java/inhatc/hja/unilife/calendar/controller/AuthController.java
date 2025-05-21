package inhatc.hja.unilife.calendar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginForm() {
        return "common/login";
    }
    
    @GetMapping("/signup")
    public String signupForm() {
        return "common/signup";
    }

    //@PostMapping("/signup")
    // 회원가입 기능 추가 예정
    
}