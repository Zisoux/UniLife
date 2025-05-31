package inhatc.hja.unilife.timetable.entity;

import inhatc.hja.unilife.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "timetables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TimetableCourse> timetableCourses = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addTimetableCourse(TimetableCourse timetableCourse) {
        timetableCourses.add(timetableCourse);
        timetableCourse.setTimetable(this);
    }

    public void removeTimetableCourse(TimetableCourse timetableCourse) {
        timetableCourses.remove(timetableCourse);
        timetableCourse.setTimetable(null);
    }
}
