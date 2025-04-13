package inhatc.hja.unilife.gpa.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "enrolled_courses")
@Data
@Getter
@Setter
public class EnrolledCourse {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "semester_id")
	private String semesterId;
	
	@Column(name = "course_id")
	private int courseId;
	
	@Column(name = "grade")
	private BigDecimal grade;
	
	@Column(name = "credits")
	private int credits;
	
	@Column(name = "course_name")
	private String courseName;
	
	@Column(name = "is_major")
	private Boolean isMajor;
}
