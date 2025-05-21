package inhatc.hja.unlife.timetable.entity;


import java.time.LocalTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter // ✅ 요거 추가!
@Setter
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseName;
    private String professor;
    private int credits;

    @Column(name = "is_major")
    private boolean isMajor;

    private String location;
    private String day;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;
}
