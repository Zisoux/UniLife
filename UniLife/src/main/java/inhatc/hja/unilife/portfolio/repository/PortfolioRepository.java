package inhatc.hja.unilife.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inhatc.hja.unilife.portfolio.repository.entity.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long>{
	// 사용자 ID별 포트폴리오 조회
    List<Portfolio> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 제목 또는 파일명에 키워드가 포함된 포트폴리오를 찾는 쿼리 메서드
    List<Portfolio> findByTitleContainingOrFileNameContaining(String title, String fileName);

    // 정렬 기준에 맞는 포트폴리오 검색
    List<Portfolio> findAllByOrderByCreatedAtDesc();
    List<Portfolio> findAllByOrderByTitleAsc();
}
