package inhatc.hja.unilife.gpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inhatc.hja.unilife.gpa.entity.EnrolledCourse;

@Repository
public interface EnrolledCourseRepository extends JpaRepository<EnrolledCourse, Integer>{
	List<EnrolledCourse> findByUserIdAndSemesterId(Long userId, String semesterId);
}
