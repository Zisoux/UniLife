package inhatc.hja.unlife.timetable.repository;

import inhatc.hja.unlife.timetable.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
