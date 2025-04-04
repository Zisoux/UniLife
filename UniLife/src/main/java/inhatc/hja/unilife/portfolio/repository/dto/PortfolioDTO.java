package inhatc.hja.unilife.portfolio.repository.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import inhatc.hja.unilife.portfolio.repository.entity.Portfolio;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortfolioDTO {
	private Long id;
    private Long userId;
    private String title;
    private String description;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private String originalFileName;
    private String fileExtension;
    private Long fileSize;
    private LocalDateTime createdAt;
    
    // Constructors
    public PortfolioDTO() {
    }
    
    public PortfolioDTO(Portfolio portfolio) {
        this.id = portfolio.getId();
        this.userId = portfolio.getUserId();
        this.title = portfolio.getTitle();
        this.description = portfolio.getDescription();
        this.startDate = portfolio.getStartDate();
        this.endDate = portfolio.getEndDate();
        this.originalFileName = portfolio.getOriginalFileName();
        this.fileExtension = portfolio.getFileExtension();
        this.fileSize = portfolio.getFileSize();
        this.createdAt = portfolio.getCreatedAt();
    }
    
    // Convert DTO to Entity
    public Portfolio toEntity() {
        Portfolio portfolio = new Portfolio();
        portfolio.setUserId(this.userId);
        portfolio.setTitle(this.title);
        portfolio.setDescription(this.description);
        portfolio.setStartDate(this.startDate);
        portfolio.setEndDate(this.endDate);
        
        return portfolio;
    }
}
