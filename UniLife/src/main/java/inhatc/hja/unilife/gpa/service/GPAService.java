package inhatc.hja.unilife.gpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import inhatc.hja.unilife.gpa.entity.GPA;
import inhatc.hja.unilife.gpa.repository.GPARepository;
import inhatc.hja.unilife.gpa.entity.Semester; // Semester enum 클래스 임포트

import java.math.BigDecimal;

@Service
public class GPAService {

    @Autowired
    private GPARepository gpaRepository;

    // GPA 계산 함수
    public GPA calculateGPA(Long userId, String semesterId, int totalCredits, int majorCredits, int electiveCredits, 
                            BigDecimal totalGPA, BigDecimal majorGPA, BigDecimal electiveGPA) {

        // Semester enum으로 변환
        Semester semester = Semester.fromString(semesterId);  // semesterCode는 '1-1', '2-1' 등의 형식

        // 각 GPA의 계산 (예시로 단순하게 합산 비율로 계산)
        BigDecimal totalGPAValue = totalGPA.multiply(BigDecimal.valueOf(totalCredits));
        BigDecimal majorGPAValue = majorGPA.multiply(BigDecimal.valueOf(majorCredits));
        BigDecimal electiveGPAValue = electiveGPA.multiply(BigDecimal.valueOf(electiveCredits));

        BigDecimal totalCreditsValue = BigDecimal.valueOf(totalCredits);
        BigDecimal majorCreditsValue = BigDecimal.valueOf(majorCredits);
        BigDecimal electiveCreditsValue = BigDecimal.valueOf(electiveCredits);

        // 전체 GPA 계산
        BigDecimal totalGPAResult = totalGPAValue.add(majorGPAValue).add(electiveGPAValue)
                                    .divide(totalCreditsValue.add(majorCreditsValue).add(electiveCreditsValue), 2, BigDecimal.ROUND_HALF_UP);

        GPA gpa = new GPA();
        gpa.setUserId(userId);
        gpa.setSemesterId(semester); // enum 타입을 설정
        gpa.setTotalCredits(totalCredits);
        gpa.setMajorCredits(majorCredits);
        gpa.setElectiveCredits(electiveCredits);
        gpa.setTotalGpa(totalGPAResult); // 최종 GPA
        gpa.setMajorGpa(majorGPA);
        gpa.setElectiveGpa(electiveGPA);

        // GPA 저장
        gpaRepository.save(gpa);
        return gpa;
    }

    // 특정 사용자의 GPA 조회
    public GPA getGPAByUserIdAndSemester(Long userId, String semesterId) {
        Semester semester = Semester.fromString(semesterId); // semesterCode를 enum으로 변환
        return gpaRepository.findByUserIdAndSemesterId(userId, semester); // 수정된 GPARepository 메서드 사용
    }
}
