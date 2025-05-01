package inhatc.hja.unilife.calendar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarViewController {

    @GetMapping("/calendar")
    public String calendarPage(Model model) {
        model.addAttribute("username", "TestUser"); // 임시값
        return "calendar/calendar";
    }
}