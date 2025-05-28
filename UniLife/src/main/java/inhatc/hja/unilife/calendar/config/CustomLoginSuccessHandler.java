package inhatc.hja.unilife.calendar.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.UserRepository;
import inhatc.hja.unilife.user.security.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // 로그인한 사용자 정보 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getUsername(); // 학번 (user_id)

        // DB에서 User 엔티티 조회
        User user = userRepository.findByUserId(userId).orElse(null);
        if (user != null) {
            // 🔥 여기서 세션에 user의 PK 저장
            request.getSession().setAttribute("loginId", user.getId()); // Long
            request.getSession().setAttribute("userId", user.getUserId()); // String
            request.getSession().setAttribute("username", user.getUsername());

        }

        // 원래 가려고 했던 URL로 이동
        response.sendRedirect("/calendar");
    }
}
