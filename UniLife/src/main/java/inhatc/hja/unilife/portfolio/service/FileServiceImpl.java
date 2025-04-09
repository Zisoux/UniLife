package inhatc.hja.unilife.portfolio.service;

import inhatc.hja.unilife.portfolio.repository.entity.FileEntity;
import inhatc.hja.unilife.portfolio.repository.FileRepository;
// import inhatc.hja.unilife.portfolio.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {

    // private static final String UPLOAD_DIR = "C:/uploads/portfolio/";

    @Autowired
    private FileRepository fileRepository;

    @Override
    public FileEntity getFileBySavedName(String savedName) {
        return fileRepository.findBySavedName(savedName).orElse(null);
    }

    @Override
    public String getOriginalFileName(String fileName) {
        Optional<FileEntity> fileEntity = fileRepository.findBySavedName(fileName);
        return fileEntity.map(FileEntity::getOriginalName).orElse(fileName);
    }

    @Override
    public ResponseEntity<Resource> downloadFileByPortfolioId(Long portfolioId) {
        Optional<FileEntity> fileEntity = fileRepository.findByPortfolioId(portfolioId);
        if (fileEntity.isEmpty()) return ResponseEntity.notFound().build();

        FileEntity fileData = fileEntity.get();
        File file = new File(fileData.getFilePath());

        if (!file.exists()) return ResponseEntity.notFound().build();

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            // 한글 파일명 깨짐 방지
            String encodedFileName = new String(fileData.getOriginalName().getBytes(StandardCharsets.UTF_8), "ISO8859-1");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                    .body(new InputStreamResource(new ByteArrayInputStream("파일 다운로드 중 오류가 발생했습니다.".getBytes(StandardCharsets.UTF_8))));
        }
    }
}

