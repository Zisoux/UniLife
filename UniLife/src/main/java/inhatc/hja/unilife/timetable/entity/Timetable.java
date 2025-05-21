package inhatc.hja.unilife.timetable.entity;

import inhatc.hja.unilife.user.repository.entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

>>>>>>> tmp-timetable
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import inhatc.hja.unilife.user.entity.User;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "timetables")
@Getter
@Setter

public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String semester;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TimetableCourse> timetableCourses = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // ✅ user_id로 외래키 매핑
    private User user;
}