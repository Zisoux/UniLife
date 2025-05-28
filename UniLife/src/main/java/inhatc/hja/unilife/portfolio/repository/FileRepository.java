package inhatc.hja.unilife.portfolio.repository;

import inhatc.hja.unilife.portfolio.repository.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    // 저장된 이름으로 조회 (UUID 등)
    Optional<FileEntity> findBySavedName(String savedName);

    // 포트폴리오 ID로 조회 (1:1 관계일 경우)
    Optional<FileEntity> findByPortfolioId(Long portfolioId);

    // 여러 파일이 연결된 경우 (1:N)
    // List<FileEntity> findAllByPortfolioId(Long portfolioId);
}

