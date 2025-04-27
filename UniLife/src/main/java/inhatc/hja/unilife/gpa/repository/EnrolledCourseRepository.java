package inhatc.hja.unilife.gpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inhatc.hja.unilife.gpa.entity.EnrolledCourse;
import inhatc.hja.unilife.gpa.entity.GPA;

@Repository
public interface EnrolledCourseRepository extends JpaRepository<EnrolledCourse, Integer>{
	List<EnrolledCourse> findByUserIdAndSemesterId(Long userId, String semesterId);
	List<EnrolledCourse> findByUserId(Long userId);
	Optional<EnrolledCourse> findByCourseName(String courseName);
    List<EnrolledCourse> findByIsMajor(boolean isMajor);
    void deleteByUserId(Long userId);  // userId로 삭제
}
