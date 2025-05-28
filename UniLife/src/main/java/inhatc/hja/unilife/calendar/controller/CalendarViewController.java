package inhatc.hja.unilife.calendar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.UserRepository;

@Controller
public class CalendarViewController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/calendar")
	public String calendarPage(Model model, Authentication authentication) {
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return "redirect:/login";
	    }

	    String userId = authentication.getName();  // user_id (문자열)
	    User user = userRepository.findByUserId(userId)
	        .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

	    model.addAttribute("user", user);
	    model.addAttribute("id", user.getId()); // user.id
	    model.addAttribute("userId", user.getUserId());   // user.user_id
	    model.addAttribute("username", user.getUsername()); // user.username
	    return "calendar/calendar";
	}
}