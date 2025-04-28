package inhatc.hja.unilife.portfolio.service;

import inhatc.hja.unilife.portfolio.repository.PortfolioRepository;
import inhatc.hja.unilife.portfolio.repository.FileRepository;
import inhatc.hja.unilife.portfolio.repository.dto.FileDTO;
import inhatc.hja.unilife.portfolio.repository.dto.PortfolioDTO;
import inhatc.hja.unilife.portfolio.repository.entity.FileEntity;
import inhatc.hja.unilife.portfolio.repository.entity.Portfolio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private FileRepository fileRepository;

    @Value("${file.upload-dir:${user.home}/uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public List<PortfolioDTO> getAllPortfolios() {
        return portfolioRepository.findAll().stream()
                .map(p -> {
                    FileEntity file = fileRepository.findByPortfolioId(p.getId()).orElse(null);
                    return new PortfolioDTO(p, file);
                })
                .collect(Collectors.toList());
    }

    public List<PortfolioDTO> getPortfoliosByUserId(Long userId) {
        return portfolioRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(p -> {
                    FileEntity file = fileRepository.findByPortfolioId(p.getId()).orElse(null);
                    return new PortfolioDTO(p, file);
                })
                .collect(Collectors.toList());
    }

    public PortfolioDTO getPortfolioById(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + id));
        FileEntity fileEntity = fileRepository.findByPortfolioId(portfolio.getId()).orElse(null);
        return new PortfolioDTO(portfolio, fileEntity);
    }

    public PortfolioDTO savePortfolio(PortfolioDTO portfolioDTO, MultipartFile file, Long loggedInUserId) {
        try {
            String originalName = file.getOriginalFilename();
            String extension = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";

            if (extension.isEmpty() || !isValidFileType(originalName)) {
                throw new RuntimeException("Invalid file type: " + originalName);
            }

            // 원본 파일명으로 저장
            String savedName = originalName;
            Path targetLocation = Paths.get(uploadDir).resolve(savedName).normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String filePath = targetLocation.toString();

            Portfolio portfolio = portfolioDTO.toEntity();
            if (portfolio.getUserId() == null) {
                portfolio.setUserId(loggedInUserId);
            }

            // 포트폴리오에 파일 정보 저장
            portfolio.setFileName(originalName);
            portfolio.setFileExtension(extension);
            portfolio.setFileSize(file.getSize());
            portfolio.setFilePath(filePath);

            portfolio = portfolioRepository.save(portfolio);

            FileEntity fileEntity = new FileEntity();
            fileEntity.setPortfolio(portfolio);
            fileEntity.setOriginalName(originalName);
            fileEntity.setSavedName(savedName);
            fileEntity.setFilePath(filePath);
            fileEntity.setExtension(extension);
            fileEntity.setSize(file.getSize());
            fileRepository.save(fileEntity);

            return new PortfolioDTO(portfolio, fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
        }
    }


    public void deletePortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + id));

        FileEntity fileEntity = fileRepository.findByPortfolioId(id).orElse(null);
        if (fileEntity != null) {
            java.io.File file = new java.io.File(fileEntity.getFilePath());
            if (file.exists()) file.delete();
            fileRepository.delete(fileEntity);
        }

        portfolioRepository.delete(portfolio);
    }

    public boolean isValidFileType(String fileName) {
        String extension = (fileName != null && fileName.contains("."))
                ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
                : "";

        Set<String> allowedExtensions = Set.of("hwpx", "docx", "java", "html", "py", "cpp", "png", "jpg", "jpeg", "csv", "pdf", "sql", "uml");
        return allowedExtensions.contains(extension);
    }

    public ResponseEntity<Resource> downloadFile(Long portfolioId) {
        FileEntity fileEntity = fileRepository.findByPortfolioId(portfolioId)
                .orElseThrow(() -> new RuntimeException("File not found for portfolioId: " + portfolioId));

        // 저장된 파일 경로 설정 (savedName 기반)
        Path filePath = Paths.get(uploadDir).resolve(fileEntity.getSavedName()).normalize().toAbsolutePath();
        File file = filePath.toFile();

        if (!file.exists()) {
            throw new RuntimeException("File not found on disk: " + fileEntity.getSavedName());
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getOriginalName() + "\"")
                .body(resource);
    }


    public List<PortfolioDTO> searchPortfolios(String sortBy, String searchKeyword) {
        List<Portfolio> portfolios;

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            portfolios = portfolioRepository.findByTitleContainingOrFileNameContaining(searchKeyword, searchKeyword);
        } else {
            if ("date".equalsIgnoreCase(sortBy)) {
                portfolios = portfolioRepository.findAllByOrderByCreatedAtDesc();
            } else if ("name".equalsIgnoreCase(sortBy)) {
                portfolios = portfolioRepository.findAllByOrderByTitleAsc();
            } else {
                portfolios = portfolioRepository.findAll();
            }
        }

        return portfolios.stream()
                .map(p -> {
                    FileEntity file = fileRepository.findByPortfolioId(p.getId()).orElse(null);
                    return new PortfolioDTO(p, file);
                })
                .collect(Collectors.toList());
    }
} 