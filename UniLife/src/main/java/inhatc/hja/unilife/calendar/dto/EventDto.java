package inhatc.hja.unilife.calendar.dto;

import java.time.LocalDateTime;

public class EventDto {
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;

    
    // 기본 생성자
    public EventDto() {
    }

    // getter & setter
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
