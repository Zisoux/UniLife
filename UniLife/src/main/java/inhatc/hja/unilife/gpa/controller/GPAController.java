package inhatc.hja.unilife.gpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    // GPA 계산 및 저장
    @PostMapping("/calculate")
    public String calculateGPA(@RequestParam(name = "userId") Long userId,
                               @RequestParam(name = "semesterId") String semesterId, // String으로 변경
                               @RequestParam(name = "totalCredits") int totalCredits,
                               @RequestParam(name = "majorCredits") int majorCredits,
                               @RequestParam(name = "electiveCredits") int electiveCredits,
                               @RequestParam(name = "totalGPA") BigDecimal totalGPA,
                               @RequestParam(name = "majorGPA") BigDecimal majorGPA,
                               @RequestParam(name = "electiveGPA") BigDecimal electiveGPA,
                               Model model) {

        // Semester enum으로 semesterCode를 변환
        GPA gpa = gpaService.calculateGPA(userId, semesterId, totalCredits, majorCredits, electiveCredits, totalGPA, majorGPA, electiveGPA);
        model.addAttribute("gpa", gpa); // 결과를 모델에 추가
        return "gpa/calculation_completed"; // 결과 페이지로 리다이렉션
    }

    // GPA 조회
    @GetMapping("/view")
    public String viewGPA(@RequestParam(name = "userId") Long userId, 
                          @RequestParam(name = "semesterId") String semesterId, // String으로 변경
                          Model model, HttpSession session) {
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
