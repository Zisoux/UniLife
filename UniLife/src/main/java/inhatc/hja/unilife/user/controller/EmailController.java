package inhatc.hja.unilife.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmailController {

    private final JavaMailSender mailSender;
    private final HttpSession session;

    @GetMapping("/send-email")
    public String sendEmail(@RequestParam(name = "email") String email) {
        String authCode = UUID.randomUUID().toString().substring(0, 6);

        // 이메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[UniLife] 이메일 인증코드");
        message.setText("인증코드는: " + authCode);
        mailSender.send(message);

        // 세션에 저장
        session.setAttribute("emailAuthCode", authCode);
        session.setAttribute("emailToVerify", email);

        return "sent";
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam(name = "email") String email,
                                         @RequestParam(name = "code") String code) {
        String savedEmail = (String) session.getAttribute("emailToVerify");
        String savedCode = (String) session.getAttribute("emailAuthCode");

        if (savedEmail != null && savedEmail.equals(email) && savedCode.equals(code)) {
            session.setAttribute("emailVerified", email); // 인증 완료
            return ResponseEntity.ok().body("verified");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
    }
}

