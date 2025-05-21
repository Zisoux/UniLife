package inhatc.hja.unlife.timetable.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long friendId;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        pending,
        accepted,
        rejected
    }
}

