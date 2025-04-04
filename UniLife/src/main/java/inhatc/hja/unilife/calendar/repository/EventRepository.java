package inhatc.hja.unilife.calendar.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import inhatc.hja.unilife.calendar.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
	@Query("SELECT e FROM Event e WHERE e.start <= :date AND e.end >= :date")
    List<Event> findEventsByDate(@Param("date") LocalDateTime date);
	
	@Query("SELECT e FROM Event e WHERE e.start < :end AND e.end > :start")
	List<Event> findEventsOverlapping(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    List<Event> findByStartBetween(LocalDateTime start, LocalDateTime end);

}
