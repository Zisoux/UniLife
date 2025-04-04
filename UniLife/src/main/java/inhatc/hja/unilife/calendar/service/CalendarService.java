// 일정 관리 서비스

package inhatc.hja.unilife.calendar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import inhatc.hja.unilife.calendar.model.Event;
import inhatc.hja.unilife.calendar.repository.EventRepository;

@Service
public class CalendarService {

    private final EventRepository eventRepository;

    public CalendarService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void addEvent(Event event) {
        eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}
