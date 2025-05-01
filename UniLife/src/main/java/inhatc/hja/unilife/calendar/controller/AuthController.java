package inhatc.hja.unilife.calendar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginForm() {
        return "calendar/login"; // login.html 템플릿 이름 (src/main/resources/templates/login.html 있어야 함)
    }
}