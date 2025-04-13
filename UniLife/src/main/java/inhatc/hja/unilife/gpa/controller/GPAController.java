package inhatc.hja.unilife.gpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import inhatc.hja.unilife.gpa.entity.EnrolledCourse;
import inhatc.hja.unilife.gpa.entity.GPA;
import inhatc.hja.unilife.gpa.service.GPAService;
import inhatc.hja.unilife.user.repository.entity.User;
import inhatc.hja.unilife.user.service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

import java.math.BigDecimal;

@Controller
@RequestMapping("/gpa")
public class GPAController {

    @Autowired
    private GPAService gpaService;
    
    @Autowired
    private UserService userService;
    
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

        // 계산된 GPA 값을 모델에 추가
        GPA gpa = gpaService.getGPAByUserIdAndSemester(userId, semesterId);
        
        // 모델에 추가하여 결과를 화면에 표시
        model.addAttribute("gpa", gpa);
        model.addAttribute("enrolledCourse", enrolledCourse);

        // `redirect`를 사용하여 결과 페이지로 이동 (다시 GET 방식으로 이동)
        return "redirect:/gpa/view?userId=" + userId + "&semesterId=" + semesterId;  // GET 방식으로 리다이렉트
    }

    // GPA 조회
    @GetMapping("/view")
    public String viewGPA(@SessionAttribute(name = "userId") Long userId, 
                          @RequestParam(name = "semesterId") String semesterId, // String으로 변경
                          Model model, HttpSession session) {
    	session.setAttribute("semesterId", semesterId);
    	
        // GPA 조회
        GPA gpa = gpaService.getGPAByUserIdAndSemester(userId, semesterId);
        if (gpa == null) {
            gpa = new GPA(); // 또는 DTO 객체
        }
        
        // ✅ 사용자 정보 조회
        User user = userService.findById(userId);
        if (user != null) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("department", user.getDepartment());
        }
        
        // GPA 데이터를 Model에 추가
        model.addAttribute("gpa", gpa);
        model.addAttribute("semesterId", semesterId);
        
        // gpa/view.html 템플릿을 반환
        return "gpa/view";
    }
}
