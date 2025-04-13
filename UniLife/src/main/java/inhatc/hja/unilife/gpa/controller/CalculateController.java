package inhatc.hja.unilife.gpa.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inhatc.hja.unilife.gpa.service.GPAService;

@RestController
@RequestMapping("api/gpa/calculate")
public class CalculateController {
	
	@Autowired
	private GPAService gpaService;
	
	@PostMapping
	public String calculateGPA(@RequestParam(name = "courseName") String courseName,
								@RequestParam(name = "credits") int credits,
								@RequestParam(name = "grade") BigDecimal grade) {
		
		
		
		return "";
	}
}
