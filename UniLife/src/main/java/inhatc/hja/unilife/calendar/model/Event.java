// 일정 엔터티

package inhatc.hja.unilife.calendar.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String start;   // ISO 8601 날짜 형식
    private String location;
    private boolean alert;  // 알림 설정 여부
}
