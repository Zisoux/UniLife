package inhatc.hja.unilife.gpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import inhatc.hja.unilife.gpa.entity.GPA;
import inhatc.hja.unilife.gpa.service.GPAService;

import org.springframework.ui.Model;

import java.math.BigDecimal;

@Controller
@RequestMapping("/gpa")
public class GPAController {

    @Autowired
    private GPAService gpaService;

    // GPA 계산 및 저장
    @PostMapping("/calculate")
    public String calculateGPA(@RequestParam(name = "userId") int userId,
                               @RequestParam(name = "semesterId") int semesterId,
                               @RequestParam(name = "totalCredits") int totalCredits,
                               @RequestParam(name = "majorCredits") int majorCredits,
                               @RequestParam(name = "electiveCredits") int electiveCredits,
                               @RequestParam(name = "totalGPA") BigDecimal totalGPA,
                               @RequestParam(name = "majorGPA") BigDecimal majorGPA,
                               @RequestParam(name = "electiveGPA") BigDecimal electiveGPA,
                               Model model) {

        GPA gpa = gpaService.calculateGPA(userId, semesterId, totalCredits, majorCredits, electiveCredits, totalGPA, majorGPA, electiveGPA);
        model.addAttribute("gpa", gpa); // 결과를 모델에 추가
        return "GPA calculation completed!";
    }

    // GPA 조회
    @GetMapping("/view")
    public String viewGPA(@RequestParam(name = "userId") int userId, 
                          @RequestParam(name = "semesterId") int semesterId, 
                          Model model) {
        // GPA 조회
        GPA gpa = gpaService.getGPAByUserIdAndSemester(userId, semesterId);
        
        // GPA 데이터를 Model에 추가
        model.addAttribute("gpa", gpa);
        
        // gpa/view.html 템플릿을 반환
        return "gpa/view"; // 뷰 이름을 반환
    }

    
}
