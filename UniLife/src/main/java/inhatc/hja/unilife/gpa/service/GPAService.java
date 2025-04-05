package inhatc.hja.unilife.gpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inhatc.hja.unilife.gpa.entity.GPA;
import inhatc.hja.unilife.gpa.repository.GPARepository;

import java.math.BigDecimal;

@Service
public class GPAService {

    @Autowired
    private GPARepository gpaRepository;

    // GPA 계산 함수
    public GPA calculateGPA(int userId, int semesterId, int totalCredits, int majorCredits, int electiveCredits, 
                            BigDecimal totalGPA, BigDecimal majorGPA, BigDecimal electiveGPA) {

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
        gpa.setSemesterId(semesterId);
        gpa.setTotal_credits(totalCredits);
        gpa.setMajor_credits(majorCredits);
        gpa.setElective_credits(electiveCredits);
        gpa.setTotal_gpa(totalGPAResult); // 최종 GPA
        gpa.setMajor_gpa(majorGPA);
        gpa.setElective_gpa(electiveGPA);

        // GPA 저장
        gpaRepository.save(gpa);
        return gpa;
    }

    // 특정 사용자의 GPA 조회
    public GPA getGPAByUserIdAndSemester(int userId, int semesterId) {
        return gpaRepository.findByUserIdAndSemesterId(userId, semesterId);
    }
}
