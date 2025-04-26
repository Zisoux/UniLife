package inhatc.hja.unilife.gpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import inhatc.hja.unilife.gpa.entity.EnrolledCourse;
import inhatc.hja.unilife.gpa.entity.GPA;
import inhatc.hja.unilife.gpa.repository.EnrolledCourseRepository;
import inhatc.hja.unilife.gpa.repository.GPARepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GPAService {

    @Autowired
    private GPARepository gpaRepository;

    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;

    private BigDecimal convertGrade(@RequestParam(name = "scoreLabel") String scoreLabel) {
        switch (scoreLabel) {
            case "A+": return BigDecimal.valueOf(4.5);
            case "A0": return BigDecimal.valueOf(4.0);
            case "B+": return BigDecimal.valueOf(3.5);
            case "B0": return BigDecimal.valueOf(3.0);
            case "C+": return BigDecimal.valueOf(2.5);
            case "C0": return BigDecimal.valueOf(2.0);
            case "D+": return BigDecimal.valueOf(1.5);
            case "D0": return BigDecimal.valueOf(1.0);
            case "F": return BigDecimal.ZERO;
            case "P": case "NP": return null;
            default: throw new IllegalArgumentException("Invalid grade label: " + scoreLabel);
        }
    }

    @Transactional
    public EnrolledCourse newCalculateGPA(@SessionAttribute(name = "userId") Long userId,
                                          @SessionAttribute(name = "semesterId") String semesterId,
                                          String scoreLabel, int credits, String courseName, boolean isMajor) {
        BigDecimal grade = convertGrade(scoreLabel);

        EnrolledCourse enrolledCourse = new EnrolledCourse();
        enrolledCourse.setUserId(userId);
        enrolledCourse.setSemesterId(semesterId);
        enrolledCourse.setCourseName(courseName);
        enrolledCourse.setCredits(credits);
        enrolledCourse.setIsMajor(isMajor);
        enrolledCourse.setGrade(grade);

        enrolledCourseRepository.save(enrolledCourse);

        String changes = "new";
        updateCreditsAfterChanges(changes, credits, isMajor, grade, courseName, userId, semesterId);
        updateGPAAfterChanges(userId, semesterId);

        return enrolledCourse;
    }

    @Transactional
    public EnrolledCourse calculateGPA(@SessionAttribute(name = "userId") Long userId,
                                       @SessionAttribute(name = "semesterId") String semesterId,
                                       BigDecimal grade, int credits, String courseName, boolean isMajor) {
        EnrolledCourse enrolledCourse = new EnrolledCourse();
        enrolledCourse.setUserId(userId);
        enrolledCourse.setSemesterId(semesterId);
        enrolledCourse.setCourseName(courseName);
        enrolledCourse.setGrade(grade);
        enrolledCourse.setCredits(credits);
        enrolledCourse.setIsMajor(isMajor);

        enrolledCourseRepository.save(enrolledCourse);

        String changes = "new";
        updateCreditsAfterChanges(changes, credits, isMajor, grade, courseName, userId, semesterId);
        updateGPAAfterChanges(userId, semesterId);

        return enrolledCourse;
    }

    @Transactional
    public void updateGPAAfterChanges(Long userId, String semesterId) {
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUserIdAndSemesterId(userId, semesterId);

        BigDecimal totalGradePoints = BigDecimal.ZERO;
        int totalCourses = 0;
        BigDecimal majorGradePoints = BigDecimal.ZERO;
        int majorCourses = 0;
        BigDecimal electiveGradePoints = BigDecimal.ZERO;
        int electiveCourses = 0;

        for (EnrolledCourse course : enrolledCourses) {
            if (course.getGrade() != null) {
                totalGradePoints = totalGradePoints.add(course.getGrade());
                totalCourses++;
                if (course.getIsMajor()) {
                    majorGradePoints = majorGradePoints.add(course.getGrade());
                    majorCourses++;
                } else {
                    electiveGradePoints = electiveGradePoints.add(course.getGrade());
                    electiveCourses++;
                }
            }
        }

        BigDecimal totalGPA = (totalCourses > 0) ? totalGradePoints.divide(BigDecimal.valueOf(totalCourses), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal majorGPA = (majorCourses > 0) ? majorGradePoints.divide(BigDecimal.valueOf(majorCourses), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal electiveGPA = (electiveCourses > 0) ? electiveGradePoints.divide(BigDecimal.valueOf(electiveCourses), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        Optional<GPA> existingGPAOpt = gpaRepository.findByUserIdAndSemesterId(userId, semesterId);
        GPA gpa = existingGPAOpt.orElse(new GPA());
        gpa.setUserId(userId);
        gpa.setSemesterId(semesterId);
        gpa.setTotalGpa(totalGPA);
        gpa.setMajorGpa(majorGPA);
        gpa.setElectiveGpa(electiveGPA);

        String previousSemester = getPreviousSemester(semesterId);
        gpaRepository.findByUserIdAndSemesterId(userId, previousSemester).ifPresent(previousGPA -> {
            gpa.setTotalChange(totalGPA.subtract(previousGPA.getTotalGpa()));
            gpa.setMajorChange(majorGPA.subtract(previousGPA.getMajorGpa()));
            gpa.setElectiveChange(electiveGPA.subtract(previousGPA.getElectiveGpa()));
        });

        gpaRepository.save(gpa);
    }

    @Transactional
    public GPA allSemesterUpdate(Long userId) {
        // 학점 합산
        int totalCreditsSum = gpaRepository.sumTotalCredits(userId);
        int majorCreditsSum = gpaRepository.sumMajorCredits(userId);
        int electiveCreditsSum = gpaRepository.sumElectiveCredits(userId);
        
        BigDecimal totalGpaSum = gpaRepository.sumTotalGpa(userId);
        BigDecimal majorGpaSum = gpaRepository.sumMajorGpa(userId);
        BigDecimal electiveGpaSum = gpaRepository.sumElectiveGpa(userId);

        int semesterCount = gpaRepository.countSemesters(userId); // 전체 학기 수
        int electiveSemesterCount = gpaRepository.countElectiveSemesters(userId); // 교양 학기 수

        if (semesterCount == 0) semesterCount = 1; // 방어 코드
        if (electiveSemesterCount == 0) electiveSemesterCount = 1; // 교양 학기 없으면 1로 방어

        // GPA 평균 계산
        BigDecimal avgTotalGpa = (totalGpaSum != null) ? totalGpaSum.divide(BigDecimal.valueOf(semesterCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal avgMajorGpa = (majorGpaSum != null) ? majorGpaSum.divide(BigDecimal.valueOf(semesterCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal avgElectiveGpa = (electiveGpaSum != null) ? electiveGpaSum.divide(BigDecimal.valueOf(electiveSemesterCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO; // ✨ 교양은 electiveSemesterCount로 나누기

        GPA allSemesterGpa = gpaRepository.findByUserIdAndSemesterId(userId, "all")
                .orElse(new GPA());

        allSemesterGpa.setUserId(userId);
        allSemesterGpa.setSemesterId("all");
        allSemesterGpa.setTotalCredits(totalCreditsSum);
        allSemesterGpa.setMajorCredits(majorCreditsSum);
        allSemesterGpa.setElectiveCredits(electiveCreditsSum);
        allSemesterGpa.setTotalGpa(avgTotalGpa);
        allSemesterGpa.setMajorGpa(avgMajorGpa);
        allSemesterGpa.setElectiveGpa(avgElectiveGpa);

        gpaRepository.save(allSemesterGpa);

        return allSemesterGpa;
    }




    @Transactional
    public void updateCreditsAfterChanges(String changes, int credits, boolean isMajor, BigDecimal grade,
                                          String courseName, @SessionAttribute(name = "userId") Long userId,
                                          @SessionAttribute(name = "semesterId") String semesterId) {
        int totalCredits = 0;
        int majorCredits = 0;
        int electiveCredits = 0;

        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUserIdAndSemesterId(userId, semesterId);

        for (EnrolledCourse course : enrolledCourses) {
            totalCredits += course.getCredits();
            if (course.getIsMajor()) majorCredits += course.getCredits();
            else electiveCredits += course.getCredits();
        }

        GPA gpa = gpaRepository.findByUserIdAndSemesterId(userId, semesterId).orElseGet(() -> {
            GPA newGpa = new GPA();
            newGpa.setUserId(userId);
            newGpa.setSemesterId(semesterId);
            return newGpa;
        });

        gpa.setTotalCredits(totalCredits);
        gpa.setMajorCredits(majorCredits);
        gpa.setElectiveCredits(electiveCredits);

        gpaRepository.save(gpa);
    }

    public GPA getGPAByUserIdAndSemester(Long userId, String semesterId) {
        return gpaRepository.findByUserIdAndSemesterId(userId, semesterId).orElse(null);
    }

    private String getPreviousSemester(String currentSemester) {
        switch (currentSemester) {
            case "1-2": return "1-1";
            case "2-1": return "1-2";
            case "2-2": return "2-1";
            case "3-1": return "2-2";
            case "3-2": return "3-1";
            case "4-1": return "3-2";
            case "4-2": return "4-1";
            case "1-1": return null; // 1-1은 이전 학기 없음
            default: throw new IllegalArgumentException("알 수 없는 학기입니다: " + currentSemester);
        }
    }

}