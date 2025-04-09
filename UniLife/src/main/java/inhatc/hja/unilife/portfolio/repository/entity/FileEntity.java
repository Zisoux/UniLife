package inhatc.hja.unilife.portfolio.repository.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "files")
@Data
@Getter
@Setter
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;
    
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

    @CreationTimestamp
    @Column(name="upload_at")
    private LocalDateTime uploadAt;

    // getters and setters 생략
}

