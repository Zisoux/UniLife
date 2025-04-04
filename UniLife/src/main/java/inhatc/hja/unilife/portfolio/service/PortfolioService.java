package inhatc.hja.unilife.portfolio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import inhatc.hja.unilife.portfolio.repository.PortfolioRepository;
import inhatc.hja.unilife.portfolio.repository.dto.PortfolioDTO;
import inhatc.hja.unilife.portfolio.repository.entity.Portfolio;
import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Value("${file.upload-dir:${user.home}/uploads}")
    private String uploadDir;
    
    // 파일 업로드 디렉토리 초기화
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));  // 지정된 경로에 디렉토리 생성
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }
    
    // 모든 포트폴리오 목록 조회
    public List<PortfolioDTO> getAllPortfolios() {
        return portfolioRepository.findAll().stream()
                .map(PortfolioDTO::new)
                .collect(Collectors.toList());
    }
    
    // 특정 사용자 포트폴리오 조회
    public List<PortfolioDTO> getPortfoliosByUserId(Long userId) {
        return portfolioRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(PortfolioDTO::new)
                .collect(Collectors.toList());
    }
    
    // ID로 포트폴리오 조회
    public PortfolioDTO getPortfolioById(Long id) {
        return portfolioRepository.findById(id)
                .map(PortfolioDTO::new)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + id));
    }
    
    // 포트폴리오 저장
    public PortfolioDTO savePortfolio(PortfolioDTO portfolioDTO, MultipartFile file, Long loggedInUserId) {
        try {
            // 파일명 및 확장자 처리
            String originalFilename = file.getOriginalFilename();
            String fileExtension = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            
            if (fileExtension.isEmpty() || !isValidFileType(originalFilename)) {
                throw new RuntimeException("Invalid file type for file: " + originalFilename);
            }

            // 고유 파일명 생성 (UUID)
            String storedFileName = UUID.randomUUID().toString() + fileExtension;

            // 파일 저장 경로 설정
            Path targetLocation = Paths.get(uploadDir).resolve(storedFileName).normalize().toAbsolutePath();

            // 파일을 저장할 디렉토리가 없으면 생성
            if (!Files.exists(targetLocation.getParent())) {
                Files.createDirectories(targetLocation.getParent());  // 디렉토리 생성
            }

            Files.copy(file.getInputStream(), targetLocation);  // 파일 저장

            // 파일 경로 저장
            String filePath = targetLocation.toString();
            
            // 포트폴리오 엔티티 생성
            Portfolio portfolio = portfolioDTO.toEntity();
            portfolio.setFilePath(filePath);
            portfolio.setOriginalFileName(originalFilename);
            portfolio.setFileExtension(fileExtension);
            portfolio.setFileSize(file.getSize());

            // 로그인한 사용자 ID 설정
            if (portfolio.getUserId() == null) {
                portfolio.setUserId(loggedInUserId);  // 로그인한 사용자 ID 설정
            }
            
            // 포트폴리오 저장
            portfolio = portfolioRepository.save(portfolio);
            
            return new PortfolioDTO(portfolio);  // DTO로 변환하여 반환
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), ex);
        }
    }

    
    // 포트폴리오 삭제
    public void deletePortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + id));
        
        // 저장된 파일 삭제
        if (portfolio.getFilePath() != null) {
            File fileToDelete = new File(portfolio.getFilePath());
            if (fileToDelete.exists()) {
                if (!fileToDelete.delete()) {
                    throw new RuntimeException("Failed to delete the file: " + fileToDelete.getAbsolutePath());
                }
            }
        }
        
        // DB에서 삭제
        portfolioRepository.delete(portfolio);
    }
    
    // 유효한 파일 형식 검사
    public boolean isValidFileType(String fileName) {
        String extension = (fileName != null && fileName.contains("."))
                ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
                : "";  // 확장자가 없는 경우 빈 문자열 처리
        
        // 허용된 파일 확장자 목록
        Set<String> allowedExtensions = Set.of("hwpx", "docx", "java", "py", "cpp", "png", "jpg", "jpeg", "csv", "pdf", "sql", "uml");

        return allowedExtensions.contains(extension);  // 허용된 확장자 목록에 포함되면 true 반환
    }
}
