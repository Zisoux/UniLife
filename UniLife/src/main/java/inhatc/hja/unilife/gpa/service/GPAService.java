package inhatc.hja.unilife.gpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.SessionAttribute;

import inhatc.hja.unilife.gpa.entity.EnrolledCourse;
import inhatc.hja.unilife.gpa.entity.GPA;
import inhatc.hja.unilife.gpa.repository.EnrolledCourseRepository;
import inhatc.hja.unilife.gpa.repository.GPARepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class GPAService {

    @Autowired
    private GPARepository gpaRepository;
    
    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;    
    

    // GPA 계산 함수
    @Transactional
    public EnrolledCourse calculateGPA(@SessionAttribute(name = "userId") Long userId, @SessionAttribute(name = "semesterId") String semesterId, BigDecimal grade, int credits, String courseName, boolean isMajor) {
        // 과목 정보를 EnrolledCourse 엔티티에 저장
        EnrolledCourse enrolledCourse = new EnrolledCourse();
        enrolledCourse.setUserId(userId);
        enrolledCourse.setSemesterId(semesterId);
        enrolledCourse.setCourseName(courseName);
        enrolledCourse.setGrade(grade);
        enrolledCourse.setCredits(credits);  // 학점 값 저장
        enrolledCourse.setIsMajor(isMajor);  // 전공 과목 여부 추가
        
        // 과목을 데이터베이스에 저장
        enrolledCourseRepository.save(enrolledCourse);
        
        String changes = "new";
        
        // 학점 추가 후 GPA 계산
        updateCreditsAfterChanges(changes, credits, isMajor, grade, courseName, userId, semesterId);  // credits 값 처리

        // GPA 계산 후 업데이트
        updateGPAAfterChanges(userId, semesterId);  // GPA 계산 후 업데이트

        return enrolledCourse;
    }
    
    // GPA 계산 후 업데이트 (성적만 기준으로 계산)
    @Transactional
    public void updateGPAAfterChanges(Long userId, String semesterId) {
        // 해당 학기 과목만 조회
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUserIdAndSemesterId(userId, semesterId); 
        
        // GPA 계산을 위한 변수 초기화
        BigDecimal totalGradePoints = BigDecimal.ZERO;
        int totalCourses = 0;
        
        BigDecimal majorGradePoints = BigDecimal.ZERO;
        int majorCourses = 0;
        
        BigDecimal electiveGradePoints = BigDecimal.ZERO;
        int electiveCourses = 0;

        // 각 과목의 성적을 이용해 GPA 계산
        for (EnrolledCourse course : enrolledCourses) {
            totalGradePoints = totalGradePoints.add(course.getGrade());  // 모든 과목의 성적 합
            totalCourses++;  // 모든 과목 수

            // 전공 과목 성적 합
            if (course.getIsMajor()) {
                majorGradePoints = majorGradePoints.add(course.getGrade());
                majorCourses++;  // 전공 과목 수
            } else {
                // 교양 과목 성적 합
                electiveGradePoints = electiveGradePoints.add(course.getGrade());
                electiveCourses++;  // 교양 과목 수
            }
        }

        // GPA 계산 (성적 평균)
        BigDecimal totalGPA = totalCourses > 0 ? totalGradePoints.divide(BigDecimal.valueOf(totalCourses), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal majorGPA = majorCourses > 0 ? majorGradePoints.divide(BigDecimal.valueOf(majorCourses), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal electiveGPA = electiveCourses > 0 ? electiveGradePoints.divide(BigDecimal.valueOf(electiveCourses), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // GPA 엔티티 조회 (해당 학기 데이터)
        GPA existingGPA = gpaRepository.findByUserIdAndSemesterId(userId, semesterId);
        if (existingGPA != null) {
            existingGPA.setTotalGpa(totalGPA);  // 전체 GPA
            existingGPA.setMajorGpa(majorGPA);  // 전공 GPA
            existingGPA.setElectiveGpa(electiveGPA);  // 교양 GPA
            
            // 성적 변화 계산 (이전 학기 GPA와 현재 GPA 비교)
            String previousSemester = getPreviousSemester(semesterId); // 이전 학기 구하기
            GPA previousGPA = gpaRepository.findByUserIdAndSemesterId(userId, previousSemester);
            
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

            // GPA 업데이트
            gpaRepository.save(existingGPA);
        } else {
            // GPA가 없으면 새로 생성하여 저장
            GPA newGPA = new GPA();
            newGPA.setUserId(userId);
            newGPA.setSemesterId(semesterId);
            newGPA.setTotalGpa(totalGPA);
            newGPA.setMajorGpa(majorGPA);
            newGPA.setElectiveGpa(electiveGPA);
            gpaRepository.save(newGPA);
        }
    }
    
    // 학점 추가 및 삭제 시 처리 -- 오류 해결!
    @Transactional
    public void updateCreditsAfterChanges(String changes, int credits, boolean isMajor, BigDecimal grade, String courseName, @SessionAttribute(name = "userId") Long userId, @SessionAttribute(name = "semesterId") String semesterId) {

        // 초기화
        int totalCredits = 0;
        int majorCredits = 0;
        int electiveCredits = 0;

        // 수정/삭제된 과목에 대한 엔티티를 데이터베이스에서 다시 조회
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUserIdAndSemesterId(userId, semesterId);

        // 과목 목록 순차적으로 확인
        for (EnrolledCourse course : enrolledCourses) {
            totalCredits += course.getCredits(); // 전체 학점 합산

            if (course.getIsMajor()) { // 전공 과목일 경우
                majorCredits += course.getCredits(); // 전공 학점 합산
            } else { // 교양 과목일 경우
                electiveCredits += course.getCredits(); // 교양 학점 합산
            }
        }

        // 변경된 학점 반영
        GPA gpa = gpaRepository.findByUserIdAndSemesterId(userId, semesterId);

        // gpa가 null이면 새로운 GPA 객체 생성
        if (gpa == null) {
            gpa = new GPA();
            gpa.setUserId(userId);
            gpa.setSemesterId(semesterId);
            gpa.setTotalCredits(0);
            gpa.setMajorCredits(0);
            gpa.setElectiveCredits(0);
        }

        // 학점 업데이트
        gpa.setTotalCredits(totalCredits);
        gpa.setMajorCredits(majorCredits);
        gpa.setElectiveCredits(electiveCredits);

        // GPA 데이터 저장
        gpaRepository.save(gpa);

        System.out.println("전체 학점: " + totalCredits);
        System.out.println("전공 학점: " + majorCredits);
        System.out.println("교양 학점: " + electiveCredits);
    }




    // 과목 수정 시 GPA 재계산
    @Transactional
    public void updateCourses(Map<String, String> courses, @SessionAttribute(name = "userId") Long userId, @SessionAttribute(name = "semesterId") String semesterId) {
        // 수정된 과목 목록을 반복
        for (Map.Entry<String, String> entry : courses.entrySet()) {
            String[] parts = entry.getKey().split("\\.");
            if (parts.length == 2) {
                int courseId = Integer.parseInt(parts[0].substring(parts[0].indexOf('[') + 1, parts[0].indexOf(']')));
                String field = parts[1];

                // 과목 찾기
                EnrolledCourse course = enrolledCourseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

                // 기존 학점
                int credits = course.getCredits();
                String changes = "update";
                
                // 수정 작업
                switch (field) {
                    case "courseName":
                        course.setCourseName(entry.getValue());
                        break;
                    case "credits":
                        updateCreditsAfterChanges(changes, credits, course.getIsMajor(), course.getGrade(), course.getCourseName(), userId, semesterId);
                        break;
                    case "grade":
                        course.setGrade(new BigDecimal(entry.getValue()));
                        break;
                    case "isMajor":
                        course.setIsMajor(Boolean.parseBoolean(entry.getValue()));
                        break;
                }

                // 과목 정보 저장
                enrolledCourseRepository.save(course);
            }
        }

        // 수정 후 GPA 재계산 및 업데이트
        updateGPAAfterChanges(userId, semesterId);
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
