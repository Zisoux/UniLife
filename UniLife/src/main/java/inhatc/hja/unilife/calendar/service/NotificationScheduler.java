package inhatc.hja.unilife.calendar.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import inhatc.hja.unilife.calendar.model.Event;
import inhatc.hja.unilife.calendar.model.EventNotification;
import inhatc.hja.unilife.calendar.repository.EventNotificationRepository;
import inhatc.hja.unilife.calendar.repository.EventRepository;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.UserRepository;

@Service
public class NotificationScheduler {

    @Autowired
    private EventNotificationRepository notiRepo;

    @Autowired
    private MailService mailService;

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private UserRepository userRepo;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkAndSendNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<EventNotification> notifications = notiRepo.findByNotifyAtBeforeAndIsSentFalse(now);

        for (EventNotification n : notifications) {
            Optional<Event> eventOpt = eventRepo.findById(n.getEventId());
            Optional<User> userOpt = userRepo.findById(n.getUserId());

            if (eventOpt.isPresent() && userOpt.isPresent()) {
                Event event = eventOpt.get();
                User user = userOpt.get();

                String to = user.getEmail();
                String subject = "[UniLife] 일정 알림 - " + event.getTitle();
                String content = String.format("⏰ %s 일정이 곧 시작됩니다! 시작 시간: %s",
                        event.getTitle(), event.getStart());

                mailService.sendMail(to, subject, content);

                n.setSent(true);
                notiRepo.save(n); // 다시 저장해서 중복 발송 방지
            }
        }
    }
}