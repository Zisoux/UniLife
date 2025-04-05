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
@Table(name = "gpa")
@Data
@Getter
@Setter
public class GPA {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "user_id")
	private int userId;
	
	@Column(name = "semester_id")
	private int semesterId;
	
	@Column(name = "total_gpa", precision = 3, scale = 2)
	private BigDecimal totalGpa;
	
	@Column(name = "major_gpa", precision = 3, scale = 2)
	private BigDecimal majorGpa;
	
	@Column(name = "elective_gpa", precision = 3, scale = 2)
	private BigDecimal electiveGpa;
	
	@Column(name = "total_credits")
	private int totalCredits;
	
	@Column(name = "major_credits")
	private int majorCredits;
	
	@Column(name = "elective_credits")
	private int electiveCredits;
}
