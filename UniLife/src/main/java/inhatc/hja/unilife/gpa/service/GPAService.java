package inhatc.hja.unilife.gpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inhatc.hja.unilife.gpa.entity.EnrolledCourse;
import inhatc.hja.unilife.gpa.entity.GPA;
import inhatc.hja.unilife.gpa.repository.EnrolledCourseRepository;
import inhatc.hja.unilife.gpa.repository.GPARepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class GPAService {

    @Autowired
    private GPARepository gpaRepository;
    
    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;

    // GPA 계산 함수
    public EnrolledCourse calculateGPA(Long userId, String semesterId, BigDecimal grade, int credits, String courseName, boolean isMajor) {

        // 과목 정보를 EnrolledCourse 엔티티에 저장
        EnrolledCourse enrolledCourse = new EnrolledCourse();
        enrolledCourse.setUserId(userId);
        enrolledCourse.setSemesterId(semesterId);
        enrolledCourse.setCourseName(courseName);
        enrolledCourse.setGrade(grade);
        enrolledCourse.setCredits(credits);
        enrolledCourse.setIsMajor(isMajor);  // 전공 과목 여부 추가
        
        // EnrolledCourse를 데이터베이스에 저장
        enrolledCourseRepository.save(enrolledCourse);

        // GPA 계산 변수 초기화
        BigDecimal totalGradePoints = BigDecimal.ZERO;  // 총 학점에 따른 성적 합
        int totalCredits = 0;  // 총 학점 수
        
        BigDecimal majorGradePoints = BigDecimal.ZERO;  // 전공 과목의 성적 합
        int majorCredits = 0;  // 전공 학점 수
        
        BigDecimal electiveGradePoints = BigDecimal.ZERO;  // 교양 과목의 성적 합
        int electiveCredits = 0;  // 교양 학점 수

        // 현재 학기의 모든 과목 정보를 가져와서 GPA 계산
        List<EnrolledCourse> grades = enrolledCourseRepository.findByUserIdAndSemesterId(userId, semesterId);

        // 각 과목의 성적과 학점을 이용해 GPA 계산
        for (EnrolledCourse course : grades) {
            BigDecimal gradePoint = course.getGrade().multiply(BigDecimal.valueOf(course.getCredits()));  // 성적 * 학점
            totalGradePoints = totalGradePoints.add(gradePoint);  // 성적 합산
            totalCredits += course.getCredits();  // 학점 합산
            
            if (course.getIsMajor()) {
                majorGradePoints = majorGradePoints.add(gradePoint);  // 전공 과목 성적 합산
                majorCredits += course.getCredits();  // 전공 학점 합산
            } else {
                electiveGradePoints = electiveGradePoints.add(gradePoint);  // 교양 과목 성적 합산
                electiveCredits += course.getCredits();  // 교양 학점 합산
            }
        }

        // GPA 계산 (성적 합산 / 총 학점 수)
        BigDecimal totalGPA = totalGradePoints.divide(BigDecimal.valueOf(totalCredits), 2, RoundingMode.HALF_UP);
        BigDecimal majorGPA = (majorCredits > 0) ? majorGradePoints.divide(BigDecimal.valueOf(majorCredits), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal electiveGPA = (electiveCredits > 0) ? electiveGradePoints.divide(BigDecimal.valueOf(electiveCredits), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

     // GPA 엔티티 조회 (해당 학기 데이터)
        GPA existingGPA = gpaRepository.findByUserIdAndSemesterId(userId, semesterId);
        if (existingGPA == null) {
            existingGPA = new GPA();
            existingGPA.setUserId(userId);
            existingGPA.setSemesterId(semesterId);
        }

        // 기존 학점에 새 학점을 더함
        existingGPA.setTotalCredits(existingGPA.getTotalCredits() + totalCredits);
        existingGPA.setMajorCredits(existingGPA.getMajorCredits() + majorCredits);
        existingGPA.setElectiveCredits(existingGPA.getElectiveCredits() + electiveCredits);

        // 새로 계산된 GPA 값 업데이트
        existingGPA.setTotalGpa(totalGPA);  // 전체 GPA
        existingGPA.setMajorGpa(majorGPA);  // 전공 GPA
        existingGPA.setElectiveGpa(electiveGPA);  // 교양 GPA

        // 성적 변화 계산 (이전 학기 GPA와 현재 GPA 비교)
        GPA previousGPA = gpaRepository.findByUserIdAndSemesterId(userId, getPreviousSemester(semesterId));
        if (previousGPA != null) {
            // 전체 GPA 성적 변화
            BigDecimal totalGradeChange = totalGPA.subtract(previousGPA.getTotalGpa());
            existingGPA.setTotalChange(totalGradeChange);  // 전체 성적 변화

            // 전공 GPA 성적 변화
            BigDecimal majorGradeChange = majorGPA.subtract(previousGPA.getMajorGpa());
            existingGPA.setMajorChange(majorGradeChange);  // 전공 성적 변화

            // 교양 GPA 성적 변화
            BigDecimal electiveGradeChange = electiveGPA.subtract(previousGPA.getElectiveGpa());
            existingGPA.setElectiveChange(electiveGradeChange);  // 교양 성적 변화
        }

        // GPA 데이터베이스에 저장
        gpaRepository.save(existingGPA);


        return enrolledCourse; // EnrolledCourse 객체 반환
    }

    // 특정 사용자의 GPA 조회
    public GPA getGPAByUserIdAndSemester(Long userId, String semesterId) {
        return gpaRepository.findByUserIdAndSemesterId(userId, semesterId);
    }

    // 이전 학기 계산 (예시: '3-1' -> '2-2')
    private String getPreviousSemester(String currentSemester) {
        try {
            // 이전 학기를 계산하는 로직
            if (currentSemester.equals("1-1")) {
                throw new IllegalArgumentException("이전 학기가 없습니다.");
            } else if (currentSemester.equals("1-2")) {
                return "1-1";
            } else if (currentSemester.equals("2-1")) {
                return "1-2";
            } else if (currentSemester.equals("2-2")) {
                return "2-1";
            } else if (currentSemester.equals("3-1")) {
                return "2-2";
            } else if (currentSemester.equals("3-2")) {
                return "3-1";
            } else if (currentSemester.equals("4-1")) {
                return "3-2";
            } else if (currentSemester.equals("4-2")) {
                return "4-1";
            } else {
                throw new IllegalArgumentException("유효하지 않은 학기 형식입니다.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("오류: " + e.getMessage());
            return "오류 발생: " + e.getMessage();  // 오류 메시지 반환
        }
    }
}

