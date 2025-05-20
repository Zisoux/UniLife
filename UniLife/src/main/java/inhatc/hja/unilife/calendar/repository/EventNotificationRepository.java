package inhatc.hja.unilife.calendar.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import inhatc.hja.unilife.calendar.model.EventNotification;

public interface EventNotificationRepository extends JpaRepository<EventNotification, Long> {
    void deleteByEventId(Long eventId);
    List<EventNotification> findByNotifyAtBeforeAndIsSentFalse(LocalDateTime time);
}
