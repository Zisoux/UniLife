package inhatc.hja.unlife.timetable.repository;


import inhatc.hja.unlife.timetable.entity.Timetable; // ✅ 올바른 import
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    Timetable findByUserId(Long userId);
}

