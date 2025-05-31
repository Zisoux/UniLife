package inhatc.hja.unilife.gpa.repository;

import java.math.BigDecimal;
//import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
   
    @Query("SELECT COALESCE(SUM(g.totalCredits), 0) FROM GPA g WHERE g.userId = :userId AND g.semesterId <> 'all'")
    int sumTotalCredits(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(g.majorCredits), 0) FROM GPA g WHERE g.userId = :userId AND g.semesterId <> 'all'")
    int sumMajorCredits(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(g.electiveCredits), 0) FROM GPA g WHERE g.userId = :userId AND g.semesterId <> 'all'")
    int sumElectiveCredits(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(g.totalGpa), 0) FROM GPA g WHERE g.userId = :userId AND g.semesterId <> 'all'")
    BigDecimal sumTotalGpa(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(g.majorGpa), 0) FROM GPA g WHERE g.userId = :userId AND g.semesterId <> 'all'")
    BigDecimal sumMajorGpa(@Param("userId") Long userId);

 // 총 교양 GPA 합 (0 초과인 학기만)
    @Query("SELECT COALESCE(SUM(g.electiveGpa), 0) FROM GPA g WHERE g.userId = :userId AND g.semesterId <> 'all' AND g.electiveGpa > 0")
    BigDecimal sumElectiveGpa(@Param("userId") Long userId);
    
 // 총 교양 GPA 학기 수 (0 초과인 학기만)
    @Query("SELECT COUNT(g) FROM GPA g WHERE g.userId = :userId AND g.semesterId <> 'all' AND g.electiveGpa > 0")
    int countElectiveSemesters(@Param("userId") Long userId);

    
    @Query("SELECT COUNT(g) FROM GPA g WHERE g.userId = :userId AND g.semesterId <> 'all'")
    int countSemesters(@Param("userId") Long userId);

    
    // 전체학기 행을 찾기 위해
    Optional<GPA> findByUserIdAndSemesterId(Long userId, String semesterId);

    void deleteAllByUserId(Long userId);
}
