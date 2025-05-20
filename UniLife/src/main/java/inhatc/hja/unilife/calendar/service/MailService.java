package inhatc.hja.unilife.calendar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String text) {
    	
    	System.out.println("📨 메일 전송 준비 중...");
        System.out.println("⏩ 수신자: " + to);
        System.out.println("⏩ 제목: " + subject);
        System.out.println("⏩ 내용: " + text);
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("unilife@no-reply.com"); // 아무거나 가능
        mailSender.send(message);
        
        System.out.println("✅ 메일 전송 완료!");
    }
}
