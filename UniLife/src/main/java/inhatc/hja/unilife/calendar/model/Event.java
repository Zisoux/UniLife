package inhatc.hja.unilife.calendar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "repeat_rule")
    private String repeatRule;

    @Column(nullable = false)
    private String title;

    private LocalDateTime start;
    private LocalDateTime end;

    private String type;
    private String location;
    private String color;
    private String alarm;

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", userId=" + userId +
                ", repeatRule='" + repeatRule + '\'' +
                ", title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", color='" + color + '\'' +
                ", alarm='" + alarm + '\'' +
                '}';
    }
}
