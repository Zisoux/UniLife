package inhatc.hja.unilife.portfolio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import inhatc.hja.unilife.portfolio.repository.PortfolioRepository;
import inhatc.hja.unilife.portfolio.repository.dto.PortfolioDTO;
import inhatc.hja.unilife.portfolio.repository.entity.Portfolio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Value("${file.upload-dir:${user.home}/uploads}")
    private String uploadDir;
    
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }
    
    public List<PortfolioDTO> getAllPortfolios() {
        return portfolioRepository.findAll().stream()
                .map(PortfolioDTO::new)
                .collect(Collectors.toList());
    }
    
    public List<PortfolioDTO> getPortfoliosByUserId(Long userId) {
        return portfolioRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(PortfolioDTO::new)
                .collect(Collectors.toList());
    }
    
    public PortfolioDTO getPortfolioById(Long id) {
        return portfolioRepository.findById(id)
                .map(PortfolioDTO::new)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + id));
    }
    
    public PortfolioDTO savePortfolio(PortfolioDTO portfolioDTO, MultipartFile file, Long loggedInUserId) {
        try {
            // 파일 처리
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String storedFileName = UUID.randomUUID().toString() + fileExtension;
            
            Path targetLocation = Paths.get(uploadDir).resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation);
            
            // 파일 경로 저장
            String filePath = targetLocation.toString();
            
            // 엔티티 저장
            Portfolio portfolio = portfolioDTO.toEntity();
            portfolio.setFilePath(filePath);
            portfolio.setOriginalFileName(originalFilename);
            portfolio.setFileExtension(fileExtension);
            portfolio.setFileSize(file.getSize());

            // 로그인한 사용자의 userId 설정
            if (portfolio.getUserId() == null) {
                portfolio.setUserId(loggedInUserId); // 로그인한 사용자 ID 설정
            }
            
            // Portfolio 객체 저장
            portfolio = portfolioRepository.save(portfolio);
            
            return new PortfolioDTO(portfolio);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), ex);
        }
    }

    
    public void deletePortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + id));
        
        // 저장된 파일 삭제
        if (portfolio.getFilePath() != null) {
            File fileToDelete = new File(portfolio.getFilePath());
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }
        
        // DB에서 삭제
        portfolioRepository.delete(portfolio);
    }
    
    public boolean isValidFileType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        // 허용된 파일 확장자 목록
        String[] allowedExtensions = {"hwpx", "docx", "java", "py", "cpp", "png", "jpg", "jpeg", "csv", "pdf", "sql", "uml"};
        
        for (String allowedExt : allowedExtensions) {
            if (extension.equals(allowedExt)) {
                return true;
            }
        }
        
        return false;
    }
}
