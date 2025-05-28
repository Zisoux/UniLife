package inhatc.hja.unilife.calendar.controller;

import inhatc.hja.unilife.calendar.dto.EventDto;
import inhatc.hja.unilife.calendar.dto.EventUpdateDto;
import inhatc.hja.unilife.calendar.model.Event;
import inhatc.hja.unilife.calendar.repository.EventRepository;
import inhatc.hja.unilife.calendar.service.CalendarService;
import inhatc.hja.unilife.user.service.FriendService;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.repository.FriendRepository;
import inhatc.hja.unilife.user.repository.UserRepository;
import inhatc.hja.unilife.user.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired private FriendRepository friendRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FriendService friendService;
    @Autowired private CalendarService calendarService;
    
    @GetMapping("/events/add")
    public String showAddEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "calendar/event_form";
    }

    @PostMapping("/events/add")
    public ResponseEntity<String> saveEvent(@RequestBody Event event, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String loginUserId = userDetails.getUsername();
            User user = userRepository.findByUserId(loginUserId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

            if (event.getEnd() != null && event.getStart() != null && event.getEnd().isBefore(event.getStart())) {
                return ResponseEntity.badRequest().body("⛔ 종료일이 시작일보다 빠릅니다.");
            }

            calendarService.addEvent(event, user);
            return ResponseEntity.ok("✅ 저장 성공");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ 저장 실패: " + e.getMessage());
        }
    }

    @GetMapping("/events")
    @ResponseBody
    public List<EventDto> getEvents(@RequestParam("start") String start,
                                    @RequestParam("end") String end,
                                    @AuthenticationPrincipal UserDetails userDetails) {
    	
    	System.out.println("✅ getEvents() 호출됨");

        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            LocalDateTime startDateTime = LocalDateTime.parse(start.substring(0, 19));
            LocalDateTime endDateTime = LocalDateTime.parse(end.substring(0, 19));
            List<Event> events = eventRepository.findEventsOverlappingByUser(startDateTime, endDateTime, userId);
            
         // 🔍 디버그 로그 추가
            for (Event e : events) {
                System.out.println("[DEBUG] Event ID: " + e.getId() + ", userId: " + e.getUserId());
            }
            
            return events.stream().map(EventDto::fromEntity).collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            return Collections.emptyList();
        }
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<Event> eventOpt = eventRepository.findById(id);

        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = eventOpt.get();

        // 🔒 삭제 권한 체크: 로그인 유저와 이벤트 소유자 일치
        if (!event.getUserId().equals(userDetails.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
        }

        eventRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/events/day")
    @ResponseBody
    public List<Event> getEventsByDay(@RequestParam("date") String date,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
            return eventRepository.findEventsOverlappingByUser(startOfDay, endOfDay, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @GetMapping("/events/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 일정이 없습니다."));

        System.out.println("[DEBUG] 로그인한 유저 ID: " + userDetails.getUser().getId());
        System.out.println("[DEBUG] 이벤트 작성자 ID: " + event.getUserId());

        // 🔒 권한 체크
        if (!event.getUserId().equals(userDetails.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        model.addAttribute("event", event);
        return "calendar/event_edit_form";
    }

    @PostMapping("/events/update")
    public ResponseEntity<Void> updateEvent(@RequestBody Event updated, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Event event = eventRepository.findById(updated.getId())
                    .orElseThrow(() -> new RuntimeException("일정 ID 없음"));

            // 🔒 로그인한 사용자와 이벤트 소유자가 일치하는지 확인
            if (!event.getUserId().equals(userDetails.getUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
            }

            // ✅ 본인 일정일 경우만 수정
            event.setTitle(updated.getTitle());
            event.setStart(updated.getStart());
            event.setEnd(updated.getEnd());
            event.setLocation(updated.getLocation());
            event.setAlarm(updated.getAlarm());
            event.setType(updated.getType());
            event.setRepeatRule(updated.getRepeatRule());

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
        return events.stream().map(EventDto::fromEntity).collect(Collectors.toList());
    }

    @GetMapping("/events/user/{userId}")
    @ResponseBody
    public List<EventDto> getEventsByUser(@PathVariable(name = "userId") Long userId) {
        List<Event> events = eventRepository.findByUserId(userId);
        return events.stream().map(EventDto::fromEntity).collect(Collectors.toList());
    }

    @GetMapping("")
    public String calendarPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";

        Long userId = Long.parseLong(userDetails.getUsername());
        User user = userRepository.findById(userId).orElseThrow();

        model.addAttribute("userId", user.getUserId());
        model.addAttribute("username", user.getUsername());
        return "calendar/calendar";
    }
}
