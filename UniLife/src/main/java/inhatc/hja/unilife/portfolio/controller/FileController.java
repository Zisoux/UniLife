package inhatc.hja.unilife.portfolio.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import inhatc.hja.unilife.portfolio.repository.FileRepository;
import inhatc.hja.unilife.portfolio.repository.entity.FileEntity;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    private final String UPLOAD_DIR = "C:/uploads/portfolio/";

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "fileName") String fileName) throws IOException {
        File file = new File(UPLOAD_DIR + fileName);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        // 원래 파일명 가져오기
        String originalName = fileRepository.findBySavedName(fileName)
                .map(FileEntity::getOriginalName)
                .orElse(fileName);

        // 한글 파일명 인코딩
        String encodedFileName = java.net.URLEncoder.encode(originalName, java.nio.charset.StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }
}
