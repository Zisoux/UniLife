package inhatc.hja.unilife.global;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.UserRepository;

@ControllerAdvice
public class GlobalUserInfoAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void setUserAttributes(HttpSession session, Model model) {
        Long userPkId = (Long) session.getAttribute("loginId");
        if (userPkId != null) {
            userRepository.findById(userPkId).ifPresent(user -> {
                model.addAttribute("user", user); // 객체 그대로 넘김
                model.addAttribute("id", user.getId());
                model.addAttribute("userId", user.getUserId());
                model.addAttribute("username", user.getUsername());
                // 디버깅을 위한 로그 추가
                System.out.println("userId: " + user.getUserId());  // 콘솔에 userId 출력
            });
        }
    }
}
