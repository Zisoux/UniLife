// 일정 엔터티

package inhatc.hja.unilife.calendar.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "repeat_rule") // <-- 바꿔주기
    private String repeat;

    private String title;            // 일정 제목
    private LocalDateTime start;     // 시작 시간
    private LocalDateTime end;       // 종료 시간
    private String type;             // 일정 유형 (강의, 포트폴리오, 스케줄 등)
    private String location;         // 위치
    private String color;            // 칼렉더 색상
    private String alarm;            // 알림 설정 (nullable 처리 추천)
    
    // 가장 중요한 AppUser 제거 및 가장 간단한 userId 활용

    // 기본 생성자
    public Event() {}

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	public Long getId() {
	    return id;
	}

	public void setId(Long id) {
	    this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getRepeat() {
	    return repeat;
	}

	public void setRepeat(String repeat) {
	    this.repeat = repeat;
	}
	
	@Override
	public String toString() {
	    return "Event{" +
	            "id=" + id +
	            ", userId=" + userId +
	            ", title='" + title + '\'' +
	            ", start=" + start +
	            ", end=" + end +
	            ", type='" + type + '\'' +
	            ", location='" + location + '\'' +
	            ", color='" + color + '\'' +
	            ", alarm='" + alarm + '\'' +
	            ", repeat='" + repeat + '\'' +
	            '}';
	}

} 