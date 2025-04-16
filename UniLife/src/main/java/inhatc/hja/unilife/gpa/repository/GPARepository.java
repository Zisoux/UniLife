package inhatc.hja.unilife.gpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import inhatc.hja.unilife.gpa.entity.GPA;

public interface GPARepository extends JpaRepository<GPA, Integer> {

    // 전공 학점 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE GPA g SET g.majorCredits = g.majorCredits + ?1 WHERE g.userId = ?2 AND g.semesterId = ?3")
    void updateMajorCredits(int creditsChange, Long userId, String semesterId);

    // 교양 학점 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE GPA g SET g.electiveCredits = g.electiveCredits + ?1 WHERE g.userId = ?2 AND g.semesterId = ?3")
    void updateElectiveCredits(int creditsChange, Long userId, String semesterId);

    // 전체 학점 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE GPA g SET g.totalCredits = g.totalCredits + ?1 WHERE g.userId = ?2 AND g.semesterId = ?3")
    void updateTotalCredits(int creditsChange, Long userId, String semesterId);

    // GPA 조회
    GPA findByUserIdAndSemesterId(Long userId, String semesterId);
   
}
