package inhatc.hja.unilife.portfolio.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import inhatc.hja.unilife.portfolio.repository.entity.FileEntity;

public interface FileService {
    FileEntity getFileBySavedName(String fileName);
    String getOriginalFileName(String fileName);
    ResponseEntity<Resource> downloadFileByPortfolioId(Long portfolioId);
}
