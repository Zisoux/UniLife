package inhatc.hja.unilife.gpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import inhatc.hja.unilife.gpa.entity.EnrolledCourse;
import inhatc.hja.unilife.gpa.entity.GPA;
import inhatc.hja.unilife.gpa.repository.EnrolledCourseRepository;
//import inhatc.hja.unilife.gpa.repository.GPARepository;
import inhatc.hja.unilife.gpa.service.GPAService;
import inhatc.hja.unilife.user.entity.User;
import inhatc.hja.unilife.user.service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/gpa")
public class GPAController {

	@Autowired
	private GPAService gpaService;
	
	//@Autowired
	//private GPARepository gpaRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private EnrolledCourseRepository enrolledCourseRepository;

	// GPA 계산 후 결과 표시
	@PostMapping("/calculate")
	public String calculateGPA(@SessionAttribute(name = "userId") Long userId,
			@SessionAttribute(name = "semesterId") String semesterId, @RequestParam(name = "scoreLabel") String scoreLabel,
			@RequestParam(name = "credits") int credits, @RequestParam(name = "courseName") String courseName,
			@RequestParam(name = "isMajor", defaultValue = "false") boolean isMajor, Model model) {
		

		// GPA 계산
		EnrolledCourse enrolledCourse = gpaService.newCalculateGPA(userId, semesterId, scoreLabel, credits, courseName,
				isMajor);
		
		BigDecimal grade = gpaService.convertGrade(scoreLabel);

		String changes = "new";
		// GPA 계산 후 학점 업데이트
		gpaService.updateCreditsAfterChanges(changes, credits, isMajor, grade, courseName, userId, semesterId); // 과목 추가 시
																												// creditsChange
																												// =
																												// credits

		// 계산된 GPA 값을 모델에 추가
		GPA gpa = gpaService.getGPAByUserIdAndSemester(userId, semesterId);

		// 모델에 추가하여 결과를 화면에 표시
		model.addAttribute("gpa", gpa);
		model.addAttribute("enrolledCourse", enrolledCourse);

		// `redirect`를 사용하여 결과 페이지로 이동 (다시 GET 방식으로 이동)
		return "redirect:/gpa/view?userId=" + userId + "&semesterId=" + semesterId; // GET 방식으로 리다이렉트
	}

	@GetMapping("/view")
	public String viewGPA(@RequestParam(name = "userId") Long userId,
			@RequestParam(name = "semesterId") String semesterId, Model model, HttpSession session) {

		// 날짜 가져오기
		LocalDate today = LocalDate.now();
		String date = today.toString(); // yyyy-MM-dd

		// 요일 가져오기
		String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

		model.addAttribute("todayDate", date);
		model.addAttribute("dayOfWeek", dayOfWeek);

		// 세션에서 userId가 없으면 파라미터로 전달된 값으로 설정
		if (session.getAttribute("userId") == null) {
			session.setAttribute("userId", userId);
		}

		// 항상 semesterId를 세션에 갱신
		session.setAttribute("semesterId", semesterId); // 세션에서 이전 값을 덮어씁니다.

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
		model.addAttribute("semesterId", semesterId); // 모델에 semesterId도 추가
		model.addAttribute("user", user); // 사용자 정보를 함께 전달

		// gpa/view.html 템플릿을 반환
		return "gpa/view";
	}
	
	@GetMapping("/allSemester")
	@ResponseBody
	public GPA getAllSemesterGpa(@RequestParam("userId") String userIdStr) {
	    Long userId = Long.parseLong(userIdStr);
	    return gpaService.allSemesterUpdate(userId);
	}

	// 과목 수정 시 처리 (전체 수정)
	@Transactional
	@PostMapping("/update")
	public String updateCourses(@RequestParam Map<String, String> courses,
	                             @SessionAttribute(name = "userId") Long userId,
	                             @SessionAttribute(name = "semesterId") String semesterId,
	                             Model model) {

	    // 로그 추가 - 전달된 courses 데이터 확인
	    System.out.println("Received courses data: " + courses);

	    // 전체학기 업데이트 요청이면 별도로 처리
	    if ("all".equals(semesterId)) {
	        GPA allSemester = gpaService.allSemesterUpdate(userId);
	        model.addAttribute("allSemester", allSemester);
	        return "redirect:/gpa/view?userId=" + userId + "&semesterId=all";
	    }

	    // 각 과목에 대해 수정 처리
	    for (Map.Entry<String, String> entry : courses.entrySet()) {
	        // 각 과목에 대한 키를 분리
	        String[] parts = entry.getKey().split("\\.");

	        if (parts.length == 2) {
	            int courseId = Integer.parseInt(parts[0].substring(parts[0].indexOf('[') + 1, parts[0].indexOf(']'))); // courseId 추출
	            String field = parts[1]; // 필드 이름(courseName, credits, grade 등)

	            // 과목 조회
	            EnrolledCourse course = enrolledCourseRepository.findById(courseId)
	                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

	            boolean oldIsMajor = course.getIsMajor(); // 이전 전공 여부
	            String changes = "update"; // 수정된 경우 "update"

	            // 필드에 따라 값 수정
	            switch (field) {
	                case "courseName":
	                    course.setCourseName(entry.getValue()); // 과목명 수정
	                    break;
	                case "credits":
	                    course.setCredits(Integer.parseInt(entry.getValue())); // 학점 수정
	                    break;
	                case "grade":
	                    // 성적은 BigDecimal로 변환 후 설정
	                    BigDecimal grade = gpaService.convertGrade(entry.getValue());
	                    course.setGrade(grade);
	                    break;
	                case "isMajor":
	                    // isMajor 체크박스의 값이 넘어오지 않으면 기본값 false 설정
	                    boolean newIsMajor = entry.getValue() != null && entry.getValue().equals("on");
	                    course.setIsMajor(newIsMajor); // 전공 여부 수정
	                    if (oldIsMajor != newIsMajor) {
	                        gpaService.updateCreditsAfterChanges(changes, course.getCredits(), newIsMajor,
	                                course.getGrade(), course.getCourseName(), userId, semesterId);
	                    }
	                    break;
	            }

	            // 수정된 course 객체 저장
	            enrolledCourseRepository.save(course);
	            enrolledCourseRepository.flush(); // 즉시 DB에 반영
	        }
	    }

	    // GPA 업데이트
	    gpaService.updateGPAAfterChanges(userId, semesterId);

	    // 기존 학기 수정 완료 후 리다이렉트
	    return "redirect:/gpa/view?userId=" + userId + "&semesterId=" + semesterId;
	}


	// ✅ 전체학기 업데이트 함수
	@Transactional
	private void allSemesterUpdate(Long userId) {
		gpaService.allSemesterUpdate(userId);
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
		enrolledCourseRepository.delete(course); // 과목 삭제

		// 삭제된 과목의 학점 변경 및 GPA 재계산
		int credits = course.getCredits();
		boolean isMajor = course.getIsMajor();
		BigDecimal grade = course.getGrade();
		String courseName = course.getCourseName();

		String changes = "delete";

		// 학점 차감 처리
		gpaService.updateCreditsAfterChanges(changes, credits, isMajor, grade, courseName, userId, semesterId); // 학점 차감

		// GPA 재계산
		gpaService.updateGPAAfterChanges(userId, semesterId); // GPA 재계산

		// 삭제 후 해당 학기의 GPA 페이지로 리다이렉트
		return "redirect:/gpa/view?userId=" + userId + "&semesterId=" + semesterId;
	}
	
	@GetMapping("/deleteAll")
	public String deleteAllData(@SessionAttribute(name="userId") Long userId) {
		enrolledCourseRepository.deleteByUserId(userId);
		return "학점, 성적 데이터 전체 삭제 완료.";
	}
}
