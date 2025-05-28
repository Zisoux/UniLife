package inhatc.hja.unilife.portfolio.repository.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import inhatc.hja.unilife.portfolio.repository.entity.FileEntity;
import inhatc.hja.unilife.portfolio.repository.entity.Portfolio;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortfolioDTO {
    private Long id;
    
    @Column(name="user_id")
    private Long userId;
    
    @Column(name="title")
    private String title;
    
    @Column(name="title")
    private String description;

    @Column(name="start_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name="end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    // 파일 정보 직접 저장
    @Column(name="file_name")
    private String fileName;
    
    @Column(name="file_extension")
    private String fileExtension;
    
    @Column(name="file_size")
    private long fileSize;
    
    @Column(name="file_path")
    private String filePath;

    public PortfolioDTO() {
    }

    public PortfolioDTO(Portfolio portfolio, FileEntity fileEntity) {
        this.id = portfolio.getId();
        this.userId = portfolio.getUserId();
        this.title = portfolio.getTitle();
        this.description = portfolio.getDescription();
        this.startDate = portfolio.getStartDate();
        this.endDate = portfolio.getEndDate();
        this.createdAt = portfolio.getCreatedAt();

        if (fileEntity != null) {
            this.fileName = fileEntity.getOriginalName();
            this.fileExtension = fileEntity.getExtension();
            this.filePath = fileEntity.getFilePath();
            this.fileSize = fileEntity.getSize();
        }
    }

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
