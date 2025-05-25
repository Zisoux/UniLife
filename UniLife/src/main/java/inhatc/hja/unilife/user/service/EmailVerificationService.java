package inhatc.hja.unilife.user.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final Map<String, VerificationInfo> verificationStorage = new ConcurrentHashMap<>();

    public EmailVerificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Getter
    @AllArgsConstructor
    private static class VerificationInfo {
        private final String code;
        private final LocalDateTime expiresAt;
    }

    public void sendVerificationCode(String email) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000); // 6자리 숫자
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(3); // 유효시간 3분
        verificationStorage.put(email, new VerificationInfo(code, expiresAt));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[UniLife] 이메일 인증코드");
        message.setText("인증코드: " + code);
        mailSender.send(message);
    }

    public boolean verifyCode(String email, String inputCode) {
        VerificationInfo info = verificationStorage.get(email);
        if (info == null) return false;
        if (info.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        return info.getCode().equals(inputCode);
    }

    public void clearCode(String email) {
        verificationStorage.remove(email);
    }
}

