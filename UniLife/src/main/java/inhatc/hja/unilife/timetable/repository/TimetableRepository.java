package inhatc.hja.unilife.timetable.repository;

import inhatc.hja.unilife.timetable.entity.Timetable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    // ❌ 이거는 필요 없으면 삭제
    // Timetable findByUserId(Long userId);

    // ✅ 고친 쿼리 (userId -> user.id)
    @Query("SELECT t FROM Timetable t LEFT JOIN FETCH t.timetableCourses WHERE t.user.id = :userId AND t.semester = :semester")
    Optional<Timetable> findByUserIdAndSemester(@Param("userId") Long userId, @Param("semester") String semester);

    // ✅ 고친 쿼리 (userId -> user.id)
    @Query("SELECT t FROM Timetable t WHERE t.user.id = :userId")
    List<Timetable> findAllByUserId(@Param("userId") Long userId);

    // ✅ 고친 쿼리 (userId -> user.id)
    @Query("SELECT t FROM Timetable t WHERE t.user.id = :userId AND t.semester = :semester")
    List<Timetable> findAllByUserIdAndSemester(@Param("userId") Long userId, @Param("semester") String semester);
}