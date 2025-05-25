package inhatc.hja.unilife.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import inhatc.hja.unilife.user.service.EmailVerificationService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmailController {

    private final EmailVerificationService emailVerificationService;

    // 이메일 인증코드 전송
    @GetMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestParam(name = "email") String email) {
        emailVerificationService.sendVerificationCode(email);
        return ResponseEntity.ok("sent");
    }

    // 인증코드 검증
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam(name = "email") String email,
                                              @RequestParam(name = "code") String code,
                                              HttpSession session) {
        boolean result = emailVerificationService.verifyCode(email, code);
        if (result) {
            session.setAttribute("emailVerified", email); // 세션에 인증된 이메일 저장
            emailVerificationService.clearCode(email);    // 인증 성공 시 삭제
            return ResponseEntity.ok("verified");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
        }
    }
}
