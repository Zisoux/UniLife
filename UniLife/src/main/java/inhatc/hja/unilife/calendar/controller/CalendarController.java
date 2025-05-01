package inhatc.hja.unilife.calendar.controller;

import inhatc.hja.unilife.calendar.dto.EventDto;
import inhatc.hja.unilife.calendar.model.Event;
import inhatc.hja.unilife.calendar.repository.EventRepository;
import inhatc.hja.unilife.user.repository.FriendRepository;
import inhatc.hja.unilife.user.service.FriendService;
import inhatc.hja.unilife.user.dto.SimpleUserDto;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/calendar")
public class CalendarController {

    private static final Long USER_ID = 1L; // ✅ 하드코딩된 로그인 유저 ID

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendService friendService;

    @GetMapping("/events/add")
    public String showAddEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "calendar/event_form";
    }

    @PostMapping("/events/add")
    public ResponseEntity<String> saveEvent(@RequestBody Event event) {
        try {
            event.setUserId(USER_ID); // ✅ userId 고정
            if (event.getEnd() != null && event.getStart() != null &&
                    event.getEnd().isBefore(event.getStart())) {
                return ResponseEntity.badRequest().body("⛔ 종료일이 시작일보다 빠릅니다.");
            }
            eventRepository.save(event);
            return ResponseEntity.ok("✅ 저장 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ 저장 실패: " + e.getMessage());
        }
    }

    @GetMapping("/events")
    @ResponseBody
    public List<EventDto> getEvents(@RequestParam("start") String start,
                                    @RequestParam("end") String end) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(start.substring(0, 19));
            LocalDateTime endDateTime   = LocalDateTime.parse(end.substring(0, 19));
            List<Event> events = eventRepository.findEventsOverlappingByUser(startDateTime, endDateTime, 1L); // ✅ 내 일정만
            return events.stream()
                    .map(e -> new EventDto(
                            String.valueOf(e.getId()),
                            e.getTitle(),
                            e.getStart(),
                            e.getEnd(),
                            e.getLocation(),
                            e.getColor(),
                            e.getType(),
                            e.getRepeat()
                    )).collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            return Collections.emptyList();
        }
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/events/day")
    @ResponseBody
    public List<Event> getEventsByDay(@RequestParam("date") String date,
                                      @RequestParam("userId") Long userId) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay   = localDate.atTime(23, 59, 59);
            return eventRepository.findEventsOverlappingByUser(startOfDay, endOfDay, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @GetMapping("/events/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 없습니다."));
        model.addAttribute("event", event);
        return "calendar/event_edit_form";
    }

    @PostMapping("/events/update")
    public ResponseEntity<Void> updateEvent(@RequestBody Event updated) {
        try {
            Event event = eventRepository.findById(updated.getId())
                    .orElseThrow(() -> new RuntimeException("일정 ID 없음"));

            event.setTitle(updated.getTitle());
            event.setStart(updated.getStart());
            event.setEnd(updated.getEnd());
            event.setLocation(updated.getLocation());
            event.setAlarm(updated.getAlarm());
            event.setType(updated.getType());
            event.setRepeat(updated.getRepeat());

            eventRepository.save(event);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/friends/{friendId}/events")
    @ResponseBody
    public List<EventDto> getFriendCalendar(@PathVariable(name = "friendId") Long friendId) {
        List<Event> events = eventRepository.findByUserId(friendId);
        return events.stream()
                .map(e -> new EventDto(
                        String.valueOf(e.getId()),
                        e.getTitle(),
                        e.getStart(),
                        e.getEnd(),
                        e.getLocation(),
                        e.getColor(),
                        e.getType(),
                        e.getRepeat()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/events/user/{userId}")
    @ResponseBody
    public List<EventDto> getEventsByUser(@PathVariable(name = "userId") Long userId) {
        List<Event> events = eventRepository.findByUserId(userId);
        return events.stream()
                .map(e -> new EventDto(
                        String.valueOf(e.getId()),
                        e.getTitle(),
                        e.getStart(),
                        e.getEnd(),
                        e.getLocation(),
                        e.getColor(),
                        e.getType(),
                        e.getRepeat()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("")
    public String calendarPage(Model model) {
        model.addAttribute("userId", USER_ID);
        model.addAttribute("username", "TestUser");
        return "calendar/calendar";
    }

}