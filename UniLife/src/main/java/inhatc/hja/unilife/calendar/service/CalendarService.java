package inhatc.hja.unilife.calendar.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inhatc.hja.unilife.calendar.model.Event;
import inhatc.hja.unilife.calendar.repository.EventRepository;

@Service
@Transactional
public class CalendarService {

    private final EventRepository eventRepository;

    public CalendarService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // ✅ 사용자별 일정 저장
    public void addEvent(Event event) {
        eventRepository.save(event);
    }

    // ✅ 사용자별 일정 조회 (로그인한 사용자 ID 기준)
    public List<Event> getEventsByUserId(Long userId) {
        return eventRepository.findByUserId(userId);
    }

    // ✅ 단일 일정 조회 (수정/삭제용)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 일정을 찾을 수 없습니다."));
    }

    // ✅ 일정 삭제
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // ✅ (선택) 관리자용 전체 일정 조회
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}
