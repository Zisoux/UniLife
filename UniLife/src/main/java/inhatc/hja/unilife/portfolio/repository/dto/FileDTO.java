package inhatc.hja.unilife.portfolio.repository.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;

public class FileDTO {
	@Column(name="id")
	private Long id;
	
	@Column(name="portfolio_id")
	private Long portfolioId;
	
	@Column(name="original_name")
	private String originalName;
	
	@Column(name="saved_name")
    private String savedName;
	
	@Column(name="file_path")
    private String filePath;
	
	@Column(name="extension")
    private String extension;
	
	@Column(name="size")
    private Long size;
	
	@Column(name="upload_at")
	private LocalDateTime uploadAt;
    
	public FileDTO(String originalName, String savedName, String filePath, String extension, Long size) {
		super();
		this.originalName = originalName;
		this.savedName = savedName;
		this.filePath = filePath;
		this.extension = extension;
		this.size = size;
	}
	
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public String getSavedName() {
		return savedName;
	}
	public void setSavedName(String savedName) {
		this.savedName = savedName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
    
    
}
