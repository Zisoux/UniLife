package inhatc.hja.unilife.timetable.repository;

import inhatc.hja.unilife.timetable.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findAllByUserId(Long userId);

    List<Timetable> findAllByUserIdAndSemester(Long userId, String semester);

    @Query("SELECT t FROM Timetable t LEFT JOIN FETCH t.timetableCourses WHERE t.user.id = :userId AND t.semester = :semester")
    Optional<Timetable> findByUserIdAndSemester(@Param("userId") Long userId, @Param("semester") String semester);
}
