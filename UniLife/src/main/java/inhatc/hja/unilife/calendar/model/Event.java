package inhatc.hja.unilife.calendar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 ID (FK 연결 없음, 단순 Long)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 일정 제목
    @Column(nullable = false)
    private String title;

    // 시작/종료 일시
    @Column(nullable = false)
    private LocalDateTime start;

    @Column
    private LocalDateTime end;

    // 일정 유형: 강의, 포트폴리오, 스케줄
    @Column(length = 50)
    private String type;

    // 장소
    private String location;

    // 색상
    private String color;

    // 알림 설정 (예: "10분 전", "없음" 등)
    private String alarm;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // 반복 여부 (예: 매주, 매달 등)
    @Column(name = "repeat_rule")
    private String repeat;

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
