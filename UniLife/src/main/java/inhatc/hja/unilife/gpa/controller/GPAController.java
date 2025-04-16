package inhatc.hja.unilife.gpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import inhatc.hja.unilife.gpa.entity.EnrolledCourse;
import inhatc.hja.unilife.gpa.entity.GPA;
import inhatc.hja.unilife.gpa.repository.EnrolledCourseRepository;
import inhatc.hja.unilife.gpa.service.GPAService;
import inhatc.hja.unilife.user.repository.entity.User;
import inhatc.hja.unilife.user.service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/gpa")
public class GPAController {

    @Autowired
    private GPAService gpaService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;
    
    // GPA 계산 후 결과 표시
    @PostMapping("/calculate")
    public String calculateGPA(@SessionAttribute(name = "userId") Long userId,
                               @SessionAttribute(name = "semesterId") String semesterId,
                               @RequestParam(name = "grade") BigDecimal grade,
                               @RequestParam(name = "credits") int credits,
                               @RequestParam(name = "courseName") String courseName,
                               @RequestParam(name = "isMajor", defaultValue = "false") boolean isMajor,
                               Model model) {
        
        // GPA 계산
        EnrolledCourse enrolledCourse = gpaService.calculateGPA(userId, semesterId, grade, credits, courseName, isMajor);
        
        String changes = "new";
        // GPA 계산 후 학점 업데이트
        gpaService.updateCreditsAfterChanges(changes, credits, isMajor, grade, courseName, userId, semesterId);  // 과목 추가 시 creditsChange = credits
        
        // 계산된 GPA 값을 모델에 추가
        GPA gpa = gpaService.getGPAByUserIdAndSemester(userId, semesterId);
        
        // 모델에 추가하여 결과를 화면에 표시
        model.addAttribute("gpa", gpa);
        model.addAttribute("enrolledCourse", enrolledCourse);

        // `redirect`를 사용하여 결과 페이지로 이동 (다시 GET 방식으로 이동)
        return "redirect:/gpa/view?userId=" + userId + "&semesterId=" + semesterId;  // GET 방식으로 리다이렉트
    }

    @GetMapping("/view")
    public String viewGPA(@RequestParam(name = "userId") Long userId, 
                          @RequestParam(name = "semesterId") String semesterId, 
                          Model model, HttpSession session) {
        
        // 세션에서 userId가 없으면 파라미터로 전달된 값으로 설정
        if (session.getAttribute("userId") == null) {
            session.setAttribute("userId", userId);
        }

        // 항상 semesterId를 세션에 갱신
        session.setAttribute("semesterId", semesterId);  // 세션에서 이전 값을 덮어씁니다.
        
        // GPA 조회
        GPA gpa = gpaService.getGPAByUserIdAndSemester(userId, semesterId);
        if (gpa == null) {
            gpa = new GPA(); // null이 반환되면 빈 객체 생성
        }

        // 사용자 정보 조회
        User user = userService.findById(userId);
        if (user != null) {
            session.setAttribute("username", user.getUsername());
            session.setAttribute("department", user.getDepartment());
        }
        
        // `EnrolledCourse` 데이터를 모델에 추가 (학점 통계 표시)
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUserIdAndSemesterId(userId, semesterId);
        model.addAttribute("enrolledCourses", enrolledCourses);

        // GPA 데이터를 Model에 추가
        model.addAttribute("gpa", gpa);
        model.addAttribute("semesterId", semesterId);  // 모델에 semesterId도 추가
        model.addAttribute("user", user); // 사용자 정보를 함께 전달
        
        // gpa/view.html 템플릿을 반환
        return "gpa/view";
    }

    // 과목 수정 시 처리 (전체 수정)
    @Transactional
    @PostMapping("/update")
    public String updateCourses(@RequestParam Map<String, String> courses, @SessionAttribute(name = "userId") Long userId, @SessionAttribute(name = "semesterId") String semesterId) {
        System.out.println("Received courses data: " + courses);
        
        // 수정된 과목 목록을 반복
        for (Map.Entry<String, String> entry : courses.entrySet()) {
            String[] parts = entry.getKey().split("\\.");
            if (parts.length == 2) {
                int courseId = Integer.parseInt(parts[0].substring(parts[0].indexOf('[') + 1, parts[0].indexOf(']')));
                String field = parts[1];

                // 과목 찾기
                EnrolledCourse course = enrolledCourseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

                // 기존 전공여부
                boolean oldIsMajor = course.getIsMajor();
                String changes = "update";

                // 수정 작업
                switch (field) {
                    case "courseName":
                        course.setCourseName(entry.getValue());
                        break;
                    case "credits":
                        // 수정된 credits만큼 더하거나 빼기
                        gpaService.updateCreditsAfterChanges(changes, course.getCredits(), course.getIsMajor(), course.getGrade(), course.getCourseName() , userId, semesterId);  // creditsChange는 차이값
                        break;
                    case "grade":
                        course.setGrade(new BigDecimal(entry.getValue()));
                        break;
                    case "isMajor":
                        boolean newIsMajor = Boolean.parseBoolean(entry.getValue());
                        course.setIsMajor(newIsMajor);
                        // 전공 여부가 변경된 경우, 전공에서 교양 혹은 교양에서 전공으로 학점을 이동
                        if (oldIsMajor != newIsMajor) {
                            gpaService.updateCreditsAfterChanges(changes, course.getCredits(), newIsMajor, course.getGrade() ,course.getCourseName(), userId, semesterId);
                        }
                        break;
                }

                // 과목 정보 저장
                enrolledCourseRepository.save(course);
            }
        }

        // 수정 후 GPA 재계산 및 업데이트
        gpaService.updateGPAAfterChanges(userId, semesterId);
        return "redirect:/gpa/view?userId=" + userId + "&semesterId=" + semesterId;
    }

    // 과목 삭제 처리 (삭제 시 GPA 업데이트)
    @GetMapping("/delete")
    public String deleteCourse(@RequestParam("courseId") int courseId, HttpSession session) {
        // 삭제할 과목을 조회
        EnrolledCourse course = enrolledCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        // 학기 정보 (semesterId)와 사용자 ID 추출
        String semesterId = course.getSemesterId();
        Long userId = course.getUserId();

        // 과목 삭제
        enrolledCourseRepository.delete(course);  // 과목 삭제

        // 삭제된 과목의 학점 변경 및 GPA 재계산
        int credits = course.getCredits();
        boolean isMajor = course.getIsMajor();
        BigDecimal grade = course.getGrade();
        String courseName = course.getCourseName();
        
        String changes = "delete";
        
        // 학점 차감 처리
        gpaService.updateCreditsAfterChanges(changes, credits, isMajor, grade, courseName, userId, semesterId);  // 학점 차감

        // GPA 재계산
        gpaService.updateGPAAfterChanges(userId, semesterId);  // GPA 재계산

        // 삭제 후 해당 학기의 GPA 페이지로 리다이렉트
        return "redirect:/gpa/view?userId=" + userId + "&semesterId=" + semesterId;
    }
}
