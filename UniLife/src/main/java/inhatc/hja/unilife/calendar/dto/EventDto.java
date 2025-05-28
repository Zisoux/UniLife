package inhatc.hja.unilife.calendar.dto;

import java.time.LocalDateTime;

import inhatc.hja.unilife.calendar.model.Event;

public class EventDto {
    private String id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String location;
    private String backgroundColor;
    private String type;
    private String repeat; // 추가
    private Long userId; // 사용자 ID 추가

    public EventDto() {}

    public EventDto(String id, String title, LocalDateTime start, LocalDateTime end, String location,
                    String backgroundColor, String type, String repeat, Long userId) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.location = location;
        this.backgroundColor = backgroundColor;
        this.type = type;
        this.repeat = repeat;
        this.userId = userId;
    }

    public static EventDto fromEntity(Event event) {
        EventDto dto = new EventDto();
        dto.setId(String.valueOf(event.getId()));
        dto.setTitle(event.getTitle());
        dto.setStart(event.getStart());
        dto.setEnd(event.getEnd());
        dto.setLocation(event.getLocation());
        dto.setBackgroundColor(event.getColor()); // or event.getColor()
        dto.setType(event.getType());
        dto.setRepeat(event.getRepeatRule());
        dto.setUserId(event.getUserId());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
