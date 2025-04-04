// 일정 관리 컨트롤러

package inhatc.hja.unilife.calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import inhatc.hja.unilife.calendar.model.Event;
import inhatc.hja.unilife.calendar.repository.EventRepository;

@Controller
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private EventRepository eventRepository;

    // 이벤트 등록 폼 보여주기
    @GetMapping("/events/add")
    public String showAddEventForm(Model model) {
        model.addAttribute("event", new Event()); // 빈 이벤트 객체를 전달
        return "calendar/event_form"; // 이벤트 등록을 위한 Thymeleaf 템플릿
    }

    // 이벤트 저장 처리 (폼 제출 처리)
    @PostMapping("/events/add")
    public String saveEvent(@ModelAttribute Event event) {
        eventRepository.save(event);
        return "calendar/close_and_refresh"; // 저장 성공 후 처리할 HTML 페이지
    }


    // 전체 일정 JSON 반환 (FullCalendar에서 호출)
    @GetMapping("/events")
    @ResponseBody
    public List<Event> getEvents(@RequestParam("start") String start,
                                 @RequestParam("end") String end) {
        try {
            // "2025-03-30T00:00:00+09:00" or "2025-03-30T00:00:00" → 앞 19자리만 자르기
            LocalDateTime startDateTime = LocalDateTime.parse(start.substring(0, 19));
            LocalDateTime endDateTime = LocalDateTime.parse(end.substring(0, 19));

            return eventRepository.findEventsOverlapping(startDateTime, endDateTime);
        } catch (DateTimeParseException e) {
            System.err.println("날짜 파싱 실패: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        System.out.println("삭제 요청 ID: " + id);
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/events/day")
    @ResponseBody
    public List<Event> getEventsByDay(@RequestParam("date") String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
            return eventRepository.findEventsOverlapping(startOfDay, endOfDay);
        } catch (DateTimeParseException e) {
            System.err.println("오른쪽 목록 날짜 파싱 실패: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @GetMapping("/events/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 일정이 없습니다."));
        model.addAttribute("event", event);
        return "calendar/event_edit_form";
    }

    @PostMapping("/events/update")
    public String updateEvent(@ModelAttribute Event event) {
        eventRepository.save(event); // ID가 있으므로 update 동작
        return "calendar/close_and_refresh";
    }



}
