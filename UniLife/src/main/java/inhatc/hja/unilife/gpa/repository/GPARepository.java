package inhatc.hja.unilife.gpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inhatc.hja.unilife.gpa.entity.GPA;

@Repository
public interface GPARepository extends JpaRepository<GPA, Integer>{
	GPA findByUserIdAndSemesterId(int userId, int semesterId);
}
