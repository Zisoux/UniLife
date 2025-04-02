package inhatc.hja.unilife.calendar.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inhatc.hja.unilife.calendar.model.Event;
import inhatc.hja.unilife.calendar.repository.EventRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;

    // 특정 날짜의 일정 조회
    @GetMapping
    public List<Event> getEvents(@RequestParam String date) {
        return eventRepository.findByStartContaining(date);
    }

    // 일정 추가
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return ResponseEntity.ok(Collections.singletonMap("message", "일정이 추가되었습니다!"));
    }
}
