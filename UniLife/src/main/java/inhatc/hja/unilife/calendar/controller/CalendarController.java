// 일정 관리 컨트롤러

package inhatc.hja.unilife.calendar.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inhatc.hja.unilife.calendar.model.Event;
import inhatc.hja.unilife.calendar.repository.EventRepository;

@RestController  // ✅ JSON 응답을 위한 API 컨트롤러
@RequestMapping("/api/calendar")
public class CalendarController {

	@Autowired
    private EventRepository eventRepository;
	
    @GetMapping("/events")
    public Map<String, Object> getEvents() {
        return Map.of("message", "이벤트 데이터 반환");
    }
    
    @PostMapping("/events/add")
    public ResponseEntity<Map<String, String>> addEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return ResponseEntity.ok(Collections.singletonMap("message", "일정이 추가되었습니다!"));
    }


}

