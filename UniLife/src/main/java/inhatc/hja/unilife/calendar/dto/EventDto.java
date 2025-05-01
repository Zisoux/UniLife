package inhatc.hja.unilife.calendar.dto;

import java.time.LocalDateTime;

public class EventDto {
    private String id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String location;
    private String backgroundColor;
    private String type;
    private String repeat; // 추가

    public EventDto() {}

    public EventDto(String id, String title, LocalDateTime start, LocalDateTime end, String location, String backgroundColor, String type, String repeat) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.location = location;
        this.backgroundColor = backgroundColor;
        this.type = type;
        this.repeat = repeat;
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

    public String getRepeat() { return repeat; }
    public void setRepeat(String repeat) { this.repeat = repeat; }

}