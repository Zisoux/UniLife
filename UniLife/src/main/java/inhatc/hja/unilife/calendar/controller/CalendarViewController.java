package inhatc.hja.unilife.calendar.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarViewController {

	@GetMapping("/calendar")
	public String calendarPage(Model model, Authentication authentication) {
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return "redirect:/login";
	    }

	    String userId = authentication.getName();
	    model.addAttribute("userId", userId);
	    return "calendar/calendar";
	}
}