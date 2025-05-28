package inhatc.hja.unilife.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import inhatc.hja.unilife.user.security.CustomUserDetails;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();  // ✅ 이 줄 추가
            model.addAttribute("username", userDetails.getName());  // ✅ 실명 저장
            model.addAttribute("id", userDetails.getUser().getId()); // ✅ 이거 추가!!
            model.addAttribute("userId", userDetails.getUser().getUserId());
        }
    }
}
