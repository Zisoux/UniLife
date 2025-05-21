package inhatc.hja.unilife.timetable.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import inhatc.hja.unilife.timetable.entity.Timetable;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    @Query("SELECT t FROM Timetable t LEFT JOIN FETCH t.timetableCourses WHERE t.user.id = :userId AND t.semester = :semester")
    Optional<Timetable> findByUserIdAndSemester(@Param("userId") Long userId, @Param("semester") String semester);

    @Query("SELECT t FROM Timetable t WHERE t.user.id = :userId")
    List<Timetable> findAllByUserId(@Param("userId") Long userId);
}
